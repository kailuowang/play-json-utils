package kailuowang.play.json
package test

import newtype._
import kailuowang.play.json.test.Definitions.{Name, Price, Product}
import play.api.libs.json._

class NewtypeSuite extends SuiteBase {

  test("NewSubType direct") {
    assertFormat(Name("blah"), JsString("blah"))
  }

  test("NewType direct") {
    assertFormat(Price(12), JsNumber(12))
  }

  test("NewType in case class") {
    implicit val f: Format[Product] = derive.oformats[Product]
    assertFormat(Product(Name("a"), Price(12)), Json.obj("name" -> "a", "price" -> 12))
  }

  test("full auto in case class") {
    import autoCached._
    assertFormat(Product(Name("a"), Price(12)), Json.obj("name" -> "a", "price" -> 12))
  }
}
