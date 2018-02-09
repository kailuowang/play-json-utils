package kailuowang.play.json

import io.estatico.newtype.{BaseNewType}
import play.api.libs.json.Format

package object newtype extends newtype1 {

  implicit def newSubTypeFormat[Base, Repr, Tag](implicit ev: Format[Repr]): Format[BaseNewType.Aux[Base, Tag, Repr]] =
    ev.asInstanceOf[Format[BaseNewType.Aux[Base, Tag, Repr]]]

}


trait newtype1 {
  implicit def newTypeFormat[Tag, Repr_](implicit ev: Format[Repr_]):
  Format[BaseNewType.Aux[AnyRef{type Repr = Repr_}, Tag, Repr_]] =
    ev.asInstanceOf[Format[BaseNewType.Aux[AnyRef{type Repr = Repr_}, Tag, Repr_]]]
}
