package org.example.service

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{HttpApp, Route}
import org.example.model.entities.{Author, Authors, Book, Books}
import org.example.routes.{AuthorRoutes, BookRoutes}
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Try

/**
  * REST API service entry point.
  */
object RestService extends HttpApp with App {

  private val db: Database = Database.forConfig("h2mem")

  private val authors = TableQuery[Authors]
  private val books = TableQuery[Books]

  Await.result(db.run(DBIO.seq(
    (authors.schema ++ books.schema).create,

    authors ++= Seq(
      Author(1, "George R. R. Martin"),
      Author(2, "J. R. R. Tolkien")
    ),

    books ++= Seq(
      Book(1, 1, "A Game of Thrones", 5),
      Book(2, 1, "A Clash of Kings", 4),
      Book(3, 1, "A Storm of Swords", 3),
      Book(4, 1, "A Feast for Crows", 2),
      Book(5, 1, "A Dance with Dragons", 1),
      Book(6, 2, "The Hobbit", 6),
      Book(7, 2, "The Lord of the Rings", 9),
      Book(8, 2, "The Silmarillion", 0)
    )
  )), Duration.Inf)

  override def route: Route =
    pathEndOrSingleSlash {
      complete("Server up and running")
    } ~
      new AuthorRoutes(db, authors, books).routes ~ new BookRoutes(db, books).routes


  val port = Try(ActorSystem("my-system").settings.config.getInt("akka.http.server.port")).getOrElse(8080)
  startServer("localhost", port)
}
