package net.liftmodules.jsonextractorng.mapping

import scala.reflect.api._
import scala.reflect.runtime.{universe=>ru}
import ru._

object Constants {
  val optionTypeConstructor = typeOf[Option[_]].typeConstructor
  val listTypeConstructor = typeOf[List[_]].typeConstructor
  val setTypeConstructor = typeOf[Set[_]].typeConstructor
  val arrayTypeConstructor = typeOf[Array[_]].typeConstructor
  val collectionTypeConstructors = Set(optionTypeConstructor, listTypeConstructor, setTypeConstructor, arrayTypeConstructor)

  val mapTypeConstructor = typeOf[Map[_, _]].typeConstructor

  val stringType = typeOf[String]
  val intType = typeOf[Int]
  val longType = typeOf[Long]
  val doubleType = typeOf[Double]
  val floatType = typeOf[Float]
  val byteType = typeOf[Byte]
  val bigIntType = typeOf[BigInt]
  val booleanType = typeOf[Boolean]
  val shortType = typeOf[Short]

  val primitiveTypes: Set[Type] = Set(
    typeOf[String],
    typeOf[Int],
    typeOf[Long],
    typeOf[Double],
    typeOf[Float],
    typeOf[Byte],
    typeOf[BigInt],
    typeOf[Boolean],
    typeOf[Short]
  )

  val heteroCollectionTypeConstructors = Set(
    typeOf[Tuple1[_]],
    typeOf[Tuple2[_, _]],
    typeOf[Tuple3[_, _, _]],
    typeOf[Tuple4[_, _, _, _]],
    typeOf[Tuple5[_, _, _, _, _]],
    typeOf[Tuple6[_, _, _, _, _, _]],
    typeOf[Tuple7[_, _, _, _, _, _, _]],
    typeOf[Tuple8[_, _, _, _, _, _, _, _]],
    typeOf[Tuple9[_, _, _, _, _, _, _, _, _]],
    typeOf[Tuple10[_, _, _, _, _, _, _, _, _, _]],

    typeOf[Tuple11[_, _, _, _, _, _, _, _, _, _, _]],
    typeOf[Tuple12[_, _, _, _, _, _, _, _, _, _, _, _]],
    typeOf[Tuple13[_, _, _, _, _, _, _, _, _, _, _, _, _]],
    typeOf[Tuple14[_, _, _, _, _, _, _, _, _, _, _, _, _, _]],
    typeOf[Tuple15[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _]],
    typeOf[Tuple16[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]],
    typeOf[Tuple17[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]],
    typeOf[Tuple18[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]],
    typeOf[Tuple19[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]],

    typeOf[Tuple20[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]],
    typeOf[Tuple21[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]],
    typeOf[Tuple22[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]]
  ).map(_.typeConstructor)
}
