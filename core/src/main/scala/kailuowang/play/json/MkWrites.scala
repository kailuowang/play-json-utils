package kailuowang
package play.json

import _root_.play.api.libs.json._
import shapeless._
import shapeless.labelled._


trait MkWrites[-A] extends OWrites[A]

object MkWrites extends MkWritesDerivation {
  def apply[A](implicit ev: MkWrites[A]): MkWrites[A] = ev
}

trait MkWritesDerivation extends MkWrites1 {
  implicit val emptyProductDerivedWrites: MkWrites[HNil] =
    instance(_ => Json.obj())

  implicit def productDerivedWrites[K <: Symbol, V, T <: HList](
    implicit key: Witness.Aux[K],
    writesV: Writes[V] OrElse MkWrites[V],
    writesT: MkWrites[T]): MkWrites[FieldType[K, V] :: T] = instance {
      case v :: t =>
        val headJson = writesV.unify.writes(v)
        val tailJson = writesT.writes(t)
        JsObject(Seq(key.value.name -> headJson)) ++ tailJson
    }

  implicit def emptyCoproductDerivedWrites: MkWrites[CNil] =
    instance(_ => sys.error("should not reach CNil"))
}

trait MkWrites1 extends MkWrites2 {
  // used when Writes[V] (a member of the coproduct) has to be derived.
  implicit def coproductDerivedWrites[K <: Symbol, V, R <: Coproduct](
    implicit key: Witness.Aux[K],
      writesV: Writes[V] OrElse MkWrites[V],
      writesR: MkWrites[R]
    ): MkWrites[FieldType[K, V] :+: R] =
      instance[FieldType[K, V] :+: R] {
        case Inl(l) =>
          JsObject(Seq(key.value.name -> writesV.unify.writes(l)))
        case Inr(r) =>
          writesR.writes(r)
      }
}

trait MkWrites2 extends MkWrites3 {

  //try eager first to solve the newtype issue with lazy
  implicit def WritesGeneric[A, R](
    implicit repr: LabelledGeneric.Aux[A, R],
      s: MkWrites[R]): MkWrites[A] =
    instance{ a =>
      s.writes(repr.to(a))
    }
}

trait MkWrites3 {
  protected def instance[A](body: A => JsObject): MkWrites[A] = new MkWrites[A] {
    def writes(a: A): JsObject = body(a)
  }

  implicit def writesGenericLazy[A, R <: Coproduct](
       implicit repr: LabelledGeneric.Aux[A, R],
       s: Lazy[MkWrites[R]]): MkWrites[A] =
    instance { a =>
      s.value.writes(repr.to(a))
    }
}
