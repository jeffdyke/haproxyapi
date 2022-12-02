package haproxyapi.conversions

import shapeless._
import shapeless.labelled._
import scala.util.Try
import haproxyapi.models._

// trait Parser[A] {
//   def parse(args: List[Map[String, Any]]): List[A]
// }
// object Parser {
//   import shapeless.Generic
//   import shapeless.{HList, HNil, ::}
//   import shapeless.Lazy
//   private def create[A](thunk: List[Map[String, Any]] => A): Parser[A] = {
//     new Parser[A] {
//       def parse(args: List[Map[String, Any]]): A = thunk(args)
//     }
//   }
//   def apply[A](implicit st: Lazy[Parser[A]]): Parser[A] = st.value

//   implicit def genericParser[A, R <: HList](
//     implicit
//     generic: Generic.Aux[A, R],
//     parser: Lazy[Parser[R]]
//   ): Parser[A] = {
//     create(args => generic.from(parser.value.parse(args)))
//   }

//   implicit def hlistParser[H, T <: HList](
//     implicit
//     hParser: Lazy[Parser[H]],
//     tParser: Parser[T]
//   ): Parser[H :: T] = {
//     create(args => hParser.value.parse(args) :: tParser.parse(args.tail))
//   }
//   implicit def hconsFromMap[K <: Symbol, V, T <: HList](implicit
//                 witness: Witness.Aux[K],
//                 typeable: Typeable[V],
//                 fromMapT: FromMap[T]): FromMap[FieldType[K, V] :: T] = new FromMap[FieldType[K, V] :: T] {
//     def apply(m: Map[String, Any]): Option[FieldType[K, V] :: T] = for {
//       v <- m.get(witness.value.name.toString)
//       r <- typeable.cast(v)
//       t <- fromMapT(m)
//     } yield field[K][V](r) :: t
//   }

//   implicit val stringParser: Parser[String] = {
//     create(args => args.head.values.head.toString())
//   }
//   implicit val fromBackend = LabelledGeneric[models.Backend]
//   implicit val intParser: Parser[Int] = {
//     create(args => args.head.values.head.toString.toInt)
//   }

//   implicit val hnilParser: Parser[HNil] = {
//     create(args => HNil)
//   }
// }

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
