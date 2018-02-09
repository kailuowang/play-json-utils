package kailuowang.play.json
package test
import Definitions._
import play.api.libs.json.{Format, JsNumber, Json}
import newtype._

class ReadsSuite extends SuiteBase {
  test("derive for coproduct") {
    val derived = derive.reads[GrandParent]
    val result = derived.reads(Json.obj("Child" -> Json.obj("a" -> "avalue", "c" -> JsNumber(3))))
    result.isSuccess shouldBe true
    result.get shouldBe Child("avalue", 3)
  }


}
