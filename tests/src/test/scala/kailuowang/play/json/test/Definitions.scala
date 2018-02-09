package kailuowang.play.json
package test

import io.estatico.newtype.{NewSubType, NewType}

import scala.annotation.tailrec

object Definitions {
  sealed trait IList[A]
  final case class ICons[A](head: A, tail: IList[A]) extends IList[A]
  final case class INil[A]() extends IList[A]


  object IList {
    def fromSeq[T](ts: Seq[T]): IList[T] =
      ts.foldRight(INil[T](): IList[T])(ICons(_, _))

    def toList[T](l: IList[T]): List[T] = {
      @tailrec def loop(il: IList[T], acc: List[T]): List[T] = il match {
        case INil() => acc.reverse
        case ICons(h, t) => loop(t, h :: acc)
      }

      loop(l, Nil)
    }
  }

  sealed trait GrandParent

  sealed trait ParentA extends GrandParent
  case class Child(a: String, c: Int) extends ParentA
  case class ParentB(a: Boolean, b: Int) extends GrandParent

  type Name = Name.Type
  object Name extends NewSubType.Default[String]

  type Price = Price.Type
  object Price extends NewType.Default[Double]


  case class Product(name: Name, price: Price)
  case class Catalog(name: Name, products: List[Product])

}
