package haproxyapi.conversions

import shapeless._
import shapeless.labelled._
import scala.util.Try
import haproxyapi.models._

object Backend{
  implicit val be = LabelledGeneric[models.Backend]
}

trait FromMap[L <: HList] {
  def apply(m: Map[String, Any]): Option[L]
}

object FromMap {
  implicit val hnilFromMap: FromMap[HNil] = new FromMap[HNil] {
    def apply(m: Map[String, Any]): Option[HNil] = Some(HNil)
  }

  implicit def hconsFromMap[K <: Symbol, V, T <: HList](implicit
                witness: Witness.Aux[K],
                typeable: Typeable[V],
                fromMapT: FromMap[T]): FromMap[FieldType[K, V] :: T] = new FromMap[FieldType[K, V] :: T] {
    def apply(m: Map[String, Any]): Option[FieldType[K, V] :: T] = for {
      v <- m.get(witness.value.name.toString)
      r <- typeable.cast(v)
      t <- fromMapT(m)
    } yield field[K][V](r) :: t
  }
}

class ParseCaseClass[A] {
  def from[R <: HList](m: Map[String, Any])(implicit
        gen: LabelledGeneric.Aux[A, R],
        fromMap: FromMap[R]): Option[A] = {
        fromMap(m).map(hl => gen.from(hl))
  }
}

object ParseCaseClass {
  def to[A]: ParseCaseClass[A] = new ParseCaseClass[A]
}
