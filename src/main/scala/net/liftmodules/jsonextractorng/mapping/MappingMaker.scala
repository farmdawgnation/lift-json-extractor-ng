package net.liftmodules.jsonextractorng.mapping

import scala.reflect.api._
import scala.reflect.runtime.{universe=>ru}
import ru._

/**
 * Generates mappings for specific types.
 */
object MappingMaker {
  import Constants._

  def makeMapping(targetType: Type): IndependentMapping = {
    targetType match {
      case colType if collectionTypeConstructors.exists(_ =:= colType.typeConstructor) =>
        Collection(
          colType.typeConstructor,
          makeMapping(colType.typeArgs(0))
        )

      case heteroColType if heteroCollectionTypeConstructors.exists(_ =:= heteroColType.typeConstructor) =>
        val componentMappings = heteroColType.typeArgs.map(makeMapping)

        HeteroCollection(
          heteroColType.typeConstructor,
          componentMappings
        )

      case dictType if dictType.typeConstructor =:= mapTypeConstructor =>
        Dictionary(makeMapping(dictType.typeArgs(0)), makeMapping(dictType.typeArgs(1)))

      case primType if primitiveTypes.exists(_ =:= primType) =>
        // .get is safe below due to the exists check above.
        // This is real weird but the Scala reflection stuff has its own equivalence
        // checker. Sometimes == works, and other times it doesn't. Doing this ensures
        // == will work in extraction bits.
        Value(primitiveTypes.find(_ =:= primType).get)

      case constructableType =>
        val constructorSymbols = targetType.decl(ru.termNames.CONSTRUCTOR).asTerm.alternatives.map(_.asMethod)

        val declaredConstructors = constructorSymbols.map { ctor =>
          if (ctor.paramLists.length > 1) {
            throw new RuntimeException("All constructors on requested type must have a single parameter list.")
          } else if (ctor.paramLists.length == 0) {
            val m = runtimeMirror(getClass.getClassLoader)
            val targetClass = constructableType.typeSymbol.asClass
            val classMirror = m.reflectClass(targetClass)
            val invokable = classMirror.reflectConstructor(ctor)

            DeclaredConstructor(invokable, Nil)
          } else {
            val arguments = ctor.paramLists(0).map { param =>
              val paramType = param.asTerm.info

              if (collectionTypeConstructors.contains(paramType.typeConstructor)) {
                Argument(param.asTerm.name.toString, makeMapping(paramType), true)
              } else {
                Argument(param.asTerm.name.toString, makeMapping(paramType), false)
              }
            }

            val m = runtimeMirror(getClass.getClassLoader)
            val targetClass = constructableType.typeSymbol.asClass
            val classMirror = m.reflectClass(targetClass)
            val invokable = classMirror.reflectConstructor(ctor)

            DeclaredConstructor(invokable, arguments)
          }
        }

        Constructor(constructableType, declaredConstructors)
    }
  }
}
