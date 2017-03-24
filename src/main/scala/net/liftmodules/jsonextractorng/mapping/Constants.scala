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
}
