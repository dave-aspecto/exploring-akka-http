# Akka HTTP sample project

This sample code was created to broad my horizons in Scala and Akka HTTP.

This a simple REST service provides two resources: Author and Book.

The endpoints are the following:

* GET list of authors
* GET list of books
* GET list of books by the author ID
* GET the author by ID
* retrieve a book by ID and increment view_count filed in DB
* GET list of authors with his/her books count
* GET list of books with sort by view count

Implementation notes:

* Response format is JSON
* Basic validation is supported (HTTP codes in responses)
* List endpoints support simple pagination

Technology stack:

* Scala 2.12
* Akka HTTP 10.0.5
* Slick 3
* H2 in-memory database

### TODO

* Enable Swagger
* Write more tests
