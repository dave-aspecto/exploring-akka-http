package org.example.model.entities

import slick.jdbc.H2Profile.api._

case class Author(id: Long, name: String)
case class AuthorExtended(id: Long, name: String, booksCount: Long)

class Authors(tag: Tag) extends Table[Author](tag, "authors") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

  def * = (id, name) <> (Author.tupled, Author.unapply)

}
