package org.example.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{Directives, Route}
import org.example.model.entities.{Author, AuthorExtended, Authors, Books}
import org.example.util.JsonSupport._
import slick.jdbc.H2Profile.api._
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.ReceiveMessageRequest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/** Authors REST API routes.<br/> Further steps: <ol> <li>Query operations have
  * to be moved to service and repository layers;</li> <li> Database as well as
  * author and books table queries are parameters for the sake of simplicity.
  * Real app will use dependency injection; </li> <li> When items 1. and 2. will
  * be done it will be possible to write fine grained tests for REST API with
  * mocked service. </li> </ol>
  */
class AuthorRoutes(
    db: Database,
    authors: TableQuery[Authors],
    books: TableQuery[Books]
) extends Directives {

  val sqs = new AmazonSQSClient()

  /** GET author list.
    *
    * @return
    *   authors.
    */
  private def getAuthorsRoute: Route = path("authors") {
    get {
      val res = sqs.receiveMessage(
        new ReceiveMessageRequest()
          .withQueueUrl(
            "https://sqs.us-east-1.amazonaws.com/731241200085/monday-poc"
          )
      )

      parameters('start.as[Long], 'size.as[Long]) { (start, size) =>
        val result = for {
          authorList <- db.run(authors.drop(start).take(size).result)
          totalCount <- db.run(authors.length.result)
        } yield (authorList, totalCount)

        onComplete(result) {
          case Success(data) =>
            data match {
              case (authorList, totalCount) =>
                respondWithHeader(
                  RawHeader("X-Total-Count", totalCount.toString)
                ) {
                  complete(authorList)
                }
            }
          case Failure(ex) =>
            complete(StatusCodes.InternalServerError, ex.getMessage)
        }
      }
    }
  }

  /** GET the author by ID.
    *
    * @return
    *   author.
    */
  private def getAuthorById: Route = path("authors" / LongNumber) { authorId =>
    get {
      rejectEmptyResponse(
        complete(db.run(authors.filter(_.id === authorId).result.headOption))
      )
    }
  }

  /** GET author list with his/her books count.
    *
    * @return
    *   author list with books count.
    */
  private def getAuthorsWithBooksCountRoute: Route =
    path("authors" / "book_counting") {
      get {
        parameters('start.as[Long], 'size.as[Long]) { (start, size) =>
          val query = (for {
            author <- authors.drop(start).take(size)
            book <- books.filter(_.authorId === author.id)
          } yield (author, book)).groupBy(_._1).map {
            case (author, booksQuery) =>
              (author, booksQuery.map(_._2.id).length)
          }

          val result = for {
            authorList <- db.run(query.result)
            totalCount <- db.run(authors.length.result)
          } yield (authorList, totalCount)

          onComplete(result) {
            case Success(data) =>
              data match {
                case (authorList, totalCount) =>
                  respondWithHeader(
                    RawHeader("X-Total-Count", totalCount.toString)
                  ) {
                    complete(authorList.map {
                      case (author: Author, booksCount) =>
                        AuthorExtended(author.id, author.name, booksCount)
                    })
                  }
              }
            case Failure(ex) =>
              complete(StatusCodes.InternalServerError, ex.getMessage)
          }
        }
      }
    }

  lazy val routes: Route =
    getAuthorsRoute ~ getAuthorById ~ getAuthorsWithBooksCountRoute

}
