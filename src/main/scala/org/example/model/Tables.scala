package org.example.model

import org.example.model.entities.{Authors, Books}
import slick.jdbc.H2Profile.api._

object Tables {
  val books = TableQuery[Books]
  val authors = TableQuery[Authors]
}
