package net.liftmodules.jsonextractorng.mapping

import scala.reflect.api._
import scala.reflect.runtime.{universe=>ru}
import ru._

/**
 * Generates mappings for specific types.
 */
object MappingMaker {
  private val optionTypeConstructor = typeOf[Option[_]].typeConstructor
  private val listTypeConstructor = typeOf[List[_]].typeConstructor
  private val setTypeConstructor = typeOf[Set[_]].typeConstructor
  private val arrayTypeConstructor = typeOf[Array[_]].typeConstructor
  private[mapping] val collectionTypeConstructors = Set(optionTypeConstructor, listTypeConstructor, setTypeConstructor, arrayTypeConstructor)

  private[mapping] val mapTypeConstructor = typeOf[Map[_, _]].typeConstructor

  private[mapping] val primitiveTypes: Set[Type] = Set(
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

  def makeMapping(targetType: Type): IndependentMapping = {
    targetType match {
      case colType if collectionTypeConstructors.contains(colType.typeConstructor) =>
        Collection(colType.typeConstructor, makeMapping(colType.typeArgs(0)))

      case dictType if dictType.typeConstructor == mapTypeConstructor =>
        Dictionary(makeMapping(dictType.typeArgs(0)), makeMapping(dictType.typeArgs(1)))

      case primType if primitiveTypes.contains(primType) =>
        Value(primType)

      case constructableType =>
        val constructorSymbols = targetType.decl(ru.termNames.CONSTRUCTOR).asTerm.alternatives.map(_.asMethod)

        val declaredConstructors = constructorSymbols.map { ctor =>
          if (ctor.paramLists.length > 1) {
            throw new RuntimeException("All constructors on requested type must have a single parameter list.")
          } else if (ctor.paramLists.length == 0) {
            DeclaredConstructor(ctor, Nil)
          } else {
            val arguments = ctor.paramLists(0).map { param =>
              val paramType = param.asTerm.info

              if (collectionTypeConstructors.contains(paramType.typeConstructor)) {
                Argument(param.asTerm.name.toString, makeMapping(paramType.typeParams(0).info), true)
              } else {
                Argument(param.asTerm.name.toString, makeMapping(paramType), false)
              }
            }

            DeclaredConstructor(ctor, arguments)
          }
        }

        Constructor(constructableType, declaredConstructors)
    }
  }
}
