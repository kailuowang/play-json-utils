package kailuowang.play.json
package test

import kailuowang.play.json.test.Definitions._
import play.api.libs.json.{Format, Json, OFormat, Reads}
import newtype._
class FormatSuite extends SuiteBase {
  test("recursive data structure") {
    implicit val df = derive.oformats[IList[String]]
    assertFormat(IList.fromSeq(Seq("a", "b")),
      """
        |{"ICons":{"head":"a","tail":{"ICons":{"head":"b","tail":{"INil":{}}}}}}
      """.stripMargin)

  }

  test("recursive data structure auto") {
    import autoCached._
    assertFormat(IList.fromSeq(Seq("a", "b")),
      """
        |{"ICons":{"head":"a","tail":{"ICons":{"head":"b","tail":{"INil":{}}}}}}
      """.stripMargin)

  }

  test("deep tree") {
    import autoCached._
    assertFormat(Foo(Bar(List(Bar2(Bar3("a"))))),
      """{ "b" : {"b2" : [ {"b3" :  { "s" : "a" }}]}} """
    )
  }


  test("tree respect existing instances with type constructor in between") {
    implicit val pf = derive.oformats[Product]
    implicit val df = derive.oformats[Catalog]

    assertFormat(Catalog(name = Name("first"), products = List(Product(Name("Cheese"), Price(12)))), Json.parse(
      """
        |{"name": "first", "products": [ {"name": "Cheese", "price": 12 } ]}"
      """.stripMargin))
  }

}
