package org.example.util

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.example.model.entities.{Author, AuthorExtended, Book}
import spray.json.DefaultJsonProtocol

/**
  * Enables spray-json Support
  */
object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val authorFormat = jsonFormat2(Author)
  implicit val authorExtendedFormat = jsonFormat3(AuthorExtended)
  implicit val bookFormat = jsonFormat4(Book)
}
