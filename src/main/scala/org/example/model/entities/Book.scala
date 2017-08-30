package org.example.model.entities

import org.example.model.Tables
import slick.jdbc.H2Profile.api._

case class Book(id: Long, authorId: Long, title: String, viewsCount: Long)

class Books(tag: Tag) extends Table[Book](tag, "books") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def authorId = column[Long]("author_id")
  def title = column[String]("title")
  def viewsCount = column[Long]("views_count")

  def * = (id, authorId, title, viewsCount) <> (Book.tupled, Book.unapply)

  def fkAuthor = foreignKey("fk_author_id", authorId, Tables.authors)(_.id, onDelete = ForeignKeyAction.Cascade)
}