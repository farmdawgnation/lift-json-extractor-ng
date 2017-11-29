package net.liftmodules.jsonextractorng

import net.liftmodules.jsonextractorng.mapping._
import net.liftmodules.jsonextractorng.mapping.Constants._
import net.liftweb.json._
import scala.reflect.api._
import scala.reflect.runtime.{universe=>ru}
import ru.{Try => _, _}
import scala.util._

object Extraction {
  implicit class ExtractionNg(underlyingJValue: JValue) {
    def extractNg[RequestedType](implicit formats: Formats, targetTypeTag: ru.TypeTag[RequestedType]): RequestedType = {
      val mapping = MappingMaker.makeMapping(targetTypeTag.tpe)
      executeMapping(underlyingJValue, mapping).asInstanceOf[RequestedType]
    }

    private[this] def executeMapping(root: JValue, mapping: Mapping)(implicit formats: Formats): Any = {
      mapping match {
        case Value(targetType) =>
          convertValueToNative(root, targetType)

        case Dictionary(keyMapping, valueMapping) =>
          root match {
            case obj @ JObject(fields) =>
              fields.foldLeft(Map[Any, Any]()) { (acc, field) =>
                val attemptKey = executeMapping(JString(field.name), keyMapping)
                val attemptValue = executeMapping(field.value, valueMapping)

                acc + (attemptKey -> attemptValue)
              }

            case otherJValue =>
              throw new Exception(s"Was expecting to see a JObject when building a Map")
          }

        case Collection(targetType, contentsMapping) =>
          root match {
            case obj @ JArray(contents) =>
              val extractedContents = contents.map(executeMapping(_, contentsMapping))

              convertCollectionToNative(extractedContents, targetType)

            case otherJValue if targetType != optionTypeConstructor =>
              throw new Exception("Was expecting to see a JArray when building a collection type")

            case otherJValue =>
              val extracted = executeMapping(otherJValue, contentsMapping)
              val filteredExtracted = List(extracted).filterNot(_ == null)

              convertCollectionToNative(filteredExtracted, targetType)
          }

        case HeteroCollection(targetType, mappings) =>
          root match {
            case JArray(contents) if contents.length > 22 =>
              throw new Exception("Can't deserialize heterogenous arrays larger than 22 items with tuples")

            case obj @ JArray(contents) =>
              val extractedContents = contents.zip(mappings).map {
                case (item, mapping) =>
                  executeMapping(item, mapping)
              }

              convertTupleToNative(extractedContents)

            case JNull | JNothing =>
              null

            case otherJValue =>
              throw new Exception("Encountered unexpected type while parsing hetero collection")
          }

        case Argument(path, mapping, optional) =>
          if (root == JNothing && optional) {
            None
          } else {
            val attempt = Try(executeMapping(root, mapping))

            attempt match {
              case Failure(_) if optional => None
              case Failure(ex) => throw ex
              case Success(null) if optional => None
              case Success(somethingElse) if optional => Some(somethingElse)
              case Success(anything) => anything
            }
          }

        case ctor @ Constructor(targetType, declaredConstructors) =>
          val customDeserializer = formats.customDeserializer(formats)

          root match {
            case obj @ JObject(_) if customDeserializer.isDefinedAt(ctor.typeInfo, obj) =>
              customDeserializer(ctor.typeInfo, obj)

            case obj @ JObject(fields) =>
              val argNames = fields.map(_.name)

              ctor.bestMatching(argNames) match {
                case Some(DeclaredConstructor(reflectCtor, args)) =>
                  val nativeArgs = args.map { argData =>
                    val fieldValue = obj.findField(_.name == argData.path).map(_.value).getOrElse(JNothing)
                    executeMapping(fieldValue, argData.mapping)
                  }

                  reflectCtor.apply(nativeArgs: _*)

                case None =>
                  throw new Exception(s"No suitable constructor found")
              }

            case otherJvalue =>
              throw new Exception(s"Was expecting to see a JObject when building a $targetType")
          }
      }
    }

    private[this] def convertValueToNative(root: JValue, targetType: Type): Any = {
      (root, targetType) match {
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

        case (JBool(innerBoolean), `booleanType`) =>
          innerBoolean

        case (JNull | JNothing, _) =>
          null

        case _ =>
          throw new Exception(s"Could not find match for $root to $targetType")
      }
    }

    private[this] def convertCollectionToNative(contents: List[Any], targetType: Type): Any = {
      targetType match {
        case `listTypeConstructor` =>
          contents

        case `setTypeConstructor` =>
          contents.toSet

        case `arrayTypeConstructor` =>
          contents.toArray

        case `optionTypeConstructor` =>
          contents.headOption
      }
    }

    private[this] def convertTupleToNative(contents: List[Any]): Any = {
      contents.length match {
        case 0 =>
          null
        case 1 =>
          (contents(0))
        case 2 =>
          (contents(0), contents(1))
        case 3 =>
          (contents(0), contents(1), contents(2))
        case 4 =>
          (contents(0), contents(1), contents(2), contents(3))
        case 5 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4))
        case 6 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4), contents(5))
        case 7 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4), contents(5), contents(6))
        case 8 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4), contents(5), contents(6), contents(7))
        case 9 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4), contents(5), contents(6), contents(7), contents(8))
        case 10 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4), contents(5), contents(6), contents(7), contents(8), contents(9))
        case 11 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4), contents(5), contents(6), contents(7), contents(8), contents(9), contents(10))
        case 12 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4), contents(5), contents(6), contents(7), contents(8), contents(9), contents(10), contents(11))
        case 13 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4), contents(5), contents(6), contents(7), contents(8), contents(9), contents(10), contents(11), contents(12))
        case 14 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4), contents(5), contents(6), contents(7), contents(8), contents(9), contents(10), contents(11), contents(12), contents(13))
        case 15 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4), contents(5), contents(6), contents(7), contents(8), contents(9), contents(10), contents(11), contents(12), contents(13), contents(14))
        case 16 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4), contents(5), contents(6), contents(7), contents(8), contents(9), contents(10), contents(11), contents(12), contents(13), contents(14), contents(15))
        case 17 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4), contents(5), contents(6), contents(7), contents(8), contents(9), contents(10), contents(11), contents(12), contents(13), contents(14), contents(15), contents(16))
        case 18 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4), contents(5), contents(6), contents(7), contents(8), contents(9), contents(10), contents(11), contents(12), contents(13), contents(14), contents(15), contents(16), contents(17))
        case 19 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4), contents(5), contents(6), contents(7), contents(8), contents(9), contents(10), contents(11), contents(12), contents(13), contents(14), contents(15), contents(16), contents(17), contents(18))
        case 20 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4), contents(5), contents(6), contents(7), contents(8), contents(9), contents(10), contents(11), contents(12), contents(13), contents(14), contents(15), contents(16), contents(17), contents(18), contents(19))
        case 21 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4), contents(5), contents(6), contents(7), contents(8), contents(9), contents(10), contents(11), contents(12), contents(13), contents(14), contents(15), contents(16), contents(17), contents(18), contents(19), contents(20))
        case 22 =>
          (contents(0), contents(1), contents(2), contents(3), contents(4), contents(5), contents(6), contents(7), contents(8), contents(9), contents(10), contents(11), contents(12), contents(13), contents(14), contents(15), contents(16), contents(17), contents(18), contents(19), contents(20), contents(21))
      }
    }
  }
}
