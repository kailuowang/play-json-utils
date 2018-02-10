package kailuowang.play.json.test

import org.scalatest.{Assertion, FunSuite, Matchers}
import play.api.libs.json.{Format, JsSuccess, JsValue, Json}

class SuiteBase extends FunSuite with Matchers {
  def assertFormat[A](a: A, json: JsValue)(implicit format: Format[A]): Assertion = {
    format.writes(a) shouldBe json
    format.reads(json).get shouldBe a
  }

  def assertFormat[A](a: A, json: String)(implicit format: Format[A]): Assertion =
    assertFormat(a, Json.parse(json))

}

