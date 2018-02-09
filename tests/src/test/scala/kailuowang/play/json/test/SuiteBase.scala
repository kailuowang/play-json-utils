package kailuowang.play.json.test

import org.scalatest.{FunSuite, Matchers}
import play.api.libs.json.{Format, JsSuccess, JsValue}

class SuiteBase extends FunSuite with Matchers {
  def assertFormat[A](a: A, json: JsValue)(implicit format: Format[A]) = {
    format.writes(a) shouldBe json
    format.reads(json).get shouldBe a
  }
}

