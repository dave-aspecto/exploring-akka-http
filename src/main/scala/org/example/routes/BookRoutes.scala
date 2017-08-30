package org.example.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{Directives, Route}
import org.example.model.entities.Books
import org.example.util.JsonSupport._
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * Books REST API routes.</br>
  * Further steps:
  * <ol>
  *   <li>Query operations have to be moved to service and repository layers;</li>
  *   <li>
  *     Database as well as author and books table queries are parameters for the sake of simplicity.
  *     Real app will use dependency injection;
  *   </li>
  *   <li>
  *     When items 1. and 2. will be done
  *     it will be possible to write fine grained tests for REST API with mocked service.
  *   </li>
  * </ol>
  */
class BookRoutes (db: Database, books: TableQuery[Books]) extends Directives {

  /**
    * GET book list with optional sorting by views count.
    *
    * @return books.
    */
  private def getBooksRoute: Route = path("books") {
    get {
      parameters('start.as[Long], 'size.as[Long], 'sort.as[String].?, 'order.as[String] ? "asc") {
        (start, size, sort, order) => sort match {
          case Some(field) => field match {
            case "viewsCount" => order match {
              case "asc"  => getBooksRouteResponse(books.drop(start).take(size).sortBy(_.viewsCount))
              case "desc" => getBooksRouteResponse(books.drop(start).take(size).sortBy(_.viewsCount.desc))
              case _ => complete(StatusCodes.BadRequest, "Only 'asc' and 'desc' orders are possible")
            }
            case _ => complete(StatusCodes.BadRequest, "Only sort by 'viewsCount' implemented in this version")
          }
          case None => getBooksRouteResponse(books.drop(start).take(size))
        }
      }
    }
  }

  /**
    * Auxiliary method to keep GET books rout DRY.
    *
    * @param query books query.
    * @return books route.
    */
  private def getBooksRouteResponse(query: Query[Books, Books#TableElementType, scala.Seq]) = {
    val result = for {
      bookList   <- db.run(query.result)
      totalCount <- db.run(books.length.result)
    } yield (bookList, totalCount)

    onComplete(result) {
      case Success(data) => data match { case (bookList, totalCount) =>
        respondWithHeader(RawHeader("X-Total-Count", totalCount.toString)) {
          complete(bookList)
        }
      }
      case Failure(ex) => complete(StatusCodes.InternalServerError, ex.getMessage)
    }
  }

  /**
    * GET book list by author ID.
    *
    * @return author's books
    */
  private def getBooksByAuthorIdRoute: Route = path("authors" / LongNumber / "books" ) { authorId =>
    get {
      parameters('start.as[Long], 'size.as[Long]) { (start, size) =>
        val query = books.filter(_.authorId === authorId)
        val result = for {
          bookList   <- db.run(query.drop(start).take(size).result)
          totalCount <- db.run(query.length.result)
        } yield (bookList, totalCount)

        onComplete(result) {
          case Success(data) => data match { case (bookList, totalCount) =>
            respondWithHeader(RawHeader("X-Total-Count", totalCount.toString)) {
              complete(bookList)
            }
          }
          case Failure(ex) => complete(StatusCodes.InternalServerError, ex.getMessage)
        }
      }
    }
  }

  /**
    * Find a book by ID and increment views count field value.
    *
    * @return book with incremented views count value.
    */
  private def getBookByIdAndIncrementViewsCount: Route = path("books" / LongNumber / "viewing") { bookId =>
    post {
      val query = books.filter(_.id === bookId)
      val bookFuture = db.run(query.result.headOption.map { bookEntity =>
        bookEntity.map( book => {
          val updateAction = query.map(_.viewsCount).update(book.viewsCount + 1)
          db.run(updateAction).map { booksUpdateCount =>
            // logger configuration omitted for brevity
            // use akka.event.Logging in a real app
            if (booksUpdateCount != 1) {
              println(s"ERROR. Updated $booksUpdateCount books")
            }
          }
          book.copy(viewsCount = book.viewsCount + 1)
        })
      })
      rejectEmptyResponse(complete(bookFuture))
    }
  }

  lazy val routes: Route = getBooksRoute ~ getBooksByAuthorIdRoute ~ getBookByIdAndIncrementViewsCount
}
