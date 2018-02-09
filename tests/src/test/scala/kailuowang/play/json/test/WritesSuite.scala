package kailuowang.play.json.test

import kailuowang.play.json.{MkWrites, derive}
import kailuowang.play.json.test.Definitions._
import play.api.libs.json._
import shapeless._
import shapeless.labelled._

class WritesSuite extends SuiteBase {

  test("derive for coproduct") {
    val derived = derive.owrites[GrandParent]
    val result = derived.writes(Child("avalue", 3))

    result shouldBe Json.obj("Child" -> Json.obj("a" -> "avalue", "c" -> JsNumber(3)))
  }

  test("respect existing instances") {
    implicit val childFormat: OWrites[Child] = new OWrites[Child] {

       def writes(o: Child): JsObject = Json.obj("a" -> 1)
    }
    val derived = derive.owrites[GrandParent]

    val gp: GrandParent = Child("avalue", 3)
    val result = derived.writes(gp)

    result shouldBe Json.obj("Child" -> Json.obj("a" -> 1))
  }

  test("auto derivation respect existing instances") {
    implicit val childFormat: OWrites[Child] = new OWrites[Child] {

       def writes(o: Child): JsObject = Json.obj("a" -> 1)
    }
    val derived = derive.owrites[GrandParent]

    val gp: GrandParent = Child("avalue", 3)
    val result = derived.writes(gp)

    result shouldBe Json.obj("Child" -> Json.obj("a" -> 1))
  }

}
