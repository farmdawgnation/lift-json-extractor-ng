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
    def extractNg[RequestedType](implicit targetTypeTag: ru.TypeTag[RequestedType]): RequestedType = {
      val mapping = MappingMaker.makeMapping(targetTypeTag.tpe)
      executeMapping(underlyingJValue, mapping).asInstanceOf[RequestedType]
    }

    private[this] def executeMapping(root: JValue, mapping: Mapping): Any = {
      mapping match {
        case Value(targetType) =>
          convertValueToNative(root, targetType)

        case Dictionary(keyMapping, valueMapping) =>
          ???

        case Collection(targetType, contentsMapping) =>
          ???

        case HeteroCollection(targetType, mappings) =>
          ???

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
          root match {
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
  }
}
