package kailuowang.play.json
package test

import kailuowang.play.json.test.Definitions._
import play.api.libs.json.{Format, Json, OFormat, Reads}
import newtype._
class FormatSuite extends SuiteBase {
  test("recursive data structure") {
    implicit val df = derive.oformats[IList[String]]
    assertFormat(IList.fromSeq(Seq("a", "b")), Json.parse(
      """
        |{"ICons":{"head":"a","tail":{"ICons":{"head":"b","tail":{"INil":{}}}}}}
      """.stripMargin))

  }

  test("recursive data structure auto") {
    import autoCached._
    assertFormat(IList.fromSeq(Seq("a", "b")), Json.parse(
      """
        |{"ICons":{"head":"a","tail":{"ICons":{"head":"b","tail":{"INil":{}}}}}}
      """.stripMargin))

  }

  test("tree respect existing instances") {
    implicit val pf = derive.oformats[Product]
    implicit val df = derive.oformats[Catalog]

    assertFormat(Catalog(name = Name("first"), products = List(Product(Name("Cheese"), Price(12)))), Json.parse(
      """
        |{"name": "first", "products": [ {"name": "Cheese", "price": 12 } ]}"
      """.stripMargin))

  }

  test("auto tree respect existing instances") {
    import autoCached._

    assertFormat(Catalog(name = Name("first"), products = List(Product(Name("Cheese"), Price(12)))), Json.parse(
      """
        |{"name": "first", "products": [ {"name": "Cheese", "price": 12 } ]}"
      """.stripMargin))

  }

}
