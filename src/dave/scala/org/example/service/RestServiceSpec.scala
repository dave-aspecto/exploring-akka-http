package org.example.service

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

/**
  * REST API service tests.
  */
class RestServiceSpec extends WordSpec with Matchers with ScalatestRouteTest {

  "RestService" should {
    "answer to any request to `/`" in {
      Get("/") ~> RestService.route ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "Server up and running"
      }
      Post("/") ~> RestService.route ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "Server up and running"
      }
    }
  }

}
