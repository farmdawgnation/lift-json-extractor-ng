package net.liftmodules.jsonextractorng

import net.liftmodules.jsonextractorng.mapping._
import net.liftmodules.jsonextractorng.mapping.Constants._
import net.liftweb.json._
import scala.reflect.api._
import scala.reflect.runtime.{universe=>ru}
import ru._

object Extraction {
  implicit class ExtractionNg(underlyingJValue: JValue) {
    def extractNg[RequestedType](implicit targetTypeTag: ru.TypeTag[RequestedType]): RequestedType = {
      val mapping = MappingMaker.makeMapping(targetTypeTag.tpe)
      executeMapping(underlyingJValue, mapping).asInstanceOf[RequestedType]
    }

    private[this] def executeMapping(root: JValue, mapping: Mapping): Any = {
      mapping match {
        case Value(targetType) =>
          convertValueToNative(root, mapping)

        case Dictionary(keyMapping, valueMapping) =>
          ???

        case Collection(targetType, contentsMapping) =>
          ???

        case HeteroCollection(targetType, mappings) =>
          ???

        case Argument(path, mapping, optional) =>
          ???

        case Constructor(targetType, declaredConstructors) =>
          ???
      }
    }

    private[this] def convertValueToNative(root: JValue, mapping: Mapping): Any = {
      (root, mapping) match {
        case (JString(innerString), `stringType`) =>
          innerString

        case (JInt(innerBigInt), `bigIntType`) =>
          innerBigInt

        case (JInt(innerBigInt), `intType`) =>
          innerBigInt.intValue

        case (JInt(innerBigInt), `longType`) =>
          innerBigInt.longValue

        case (JInt(innerBigInt), `doubleType`) =>
          innerBigInt.doubleValue

        case (JInt(innerBigInt), `floatType`) =>
          innerBigInt.floatValue

        case (JInt(innerBigInt), `byteType`) =>
          innerBigInt.byteValue

        case (JInt(innerBigInt), `shortType`) =>
          innerBigInt.shortValue

        case _ =>
          throw new Exception(s"Could not find match for $mapping")
      }
    }
  }
}
