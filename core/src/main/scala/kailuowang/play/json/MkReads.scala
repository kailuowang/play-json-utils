package kailuowang
package play.json

import _root_.play.api.libs.json._

import shapeless._
import shapeless.labelled.{FieldType, field}


trait MkReads[A] extends Reads[A]

object MkReads extends MkReadsDerivation {
  def apply[A](implicit ev: MkReads[A]): MkReads[A] = ev
}

trait MkReadsDerivation extends MkReads1 {
  implicit val emptyProductDerivedReads: MkReads[HNil] =
    instance(_ => JsSuccess(HNil))

  implicit def productDerivedReads[K <: Symbol, V, T <: HList](
    implicit key: Witness.Aux[K],
    readsV: Reads[V] OrElse MkReads[V],
    readsT: MkReads[T]): MkReads[FieldType[K, V] :: T] = instance { json =>
    (__ \ key.value.name).read(readsV.unify).reads(json).flatMap { h =>
      readsT.reads(json).map(t => field[K](h) :: t)
    }
  }


  implicit def emptyCoproductDerivedReads: MkReads[CNil] =
    instance(_ => JsError("CNil"))

}

trait MkReads1 extends MkReads2 {
  // used when Reads[V] (a member of the coproduct) has to be derived.
  implicit def coproductDerivedReads[K <: Symbol, V, R <: Coproduct](
    implicit key: Witness.Aux[K],
    readsV: Reads[V] OrElse MkReads[V],
    readsL: MkReads[R]): MkReads[FieldType[K, V] :+: R] = {
    val reads =
      Reads.alternative.|(
        (__ \ key.value.name).read(readsV.unify)
          .map[FieldType[K, V] :+: R](v => {Inl(field[K](v))}),
        readsL.map[FieldType[K, V] :+: R](r => Inr(r)))
    instance { json => reads.reads(json) }
  }

}
trait MkReads2 extends MkReads3 {

  //try eager first to solve the newtype issue with lazy
  implicit def readsGeneric[A, R](
    implicit repr: LabelledGeneric.Aux[A, R],
      s: MkReads[R]): MkReads[A] =
    instance(json => s.reads(json).map(repr.from))

}

trait MkReads3 {
  protected def instance[A](body: JsValue => JsResult[A]): MkReads[A] = new MkReads[A] {
    def reads(json: JsValue): JsResult[A] = body(json)
  }

  implicit def readsGenericLazy[A, R](
                                       implicit repr: LabelledGeneric.Aux[A, R],
                                       s: Lazy[MkReads[R]]): MkReads[A] =
    instance(json => s.value.reads(json).map(repr.from))
}
