package kailuowang.play

import play.api.libs.json._
import shapeless._

package object json {
  object derive {
    def reads[A](implicit A: MkReads[A]): Reads[A] = A
    def owrites[A](implicit A: MkWrites[A]): OWrites[A] = A
    def oformats[A](implicit r: MkReads[A], w: MkWrites[A]): OFormat[A] = OFormat(r, w)
  }

  object auto  {
    implicit def autoOFormatsDerive[A: MkWrites : MkReads](
      implicit refute: Refute[OFormat[A]]
    ): OFormat[A] = derive.oformats
  }

  object autoCached  {
    implicit def autoOFormatsDerive[A](
      implicit
        refuteR: Refute[Writes[A]],
        refuteW: Refute[Reads[A]],
      readsCache: Cached[MkReads[A]],
      writesCache: Cached[MkWrites[A]]
    ): OFormat[A] = OFormat(readsCache.value, writesCache.value)
  }


}

