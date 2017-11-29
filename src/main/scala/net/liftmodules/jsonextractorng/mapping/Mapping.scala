package net.liftmodules.jsonextractorng.mapping

import java.lang.reflect.ParameterizedType
import net.liftweb.json.TypeInfo
import scala.reflect.api._
import scala.reflect.runtime.{universe=>ru}
import ru._

/**
 * Abstract class defining a Mapping rule from JValue to concrete class. The lift-json extractor
 * is a two-phase extractor. The first phase builds the Mapping AST that instructs the extractor
 * how to handle the incoming JValue. That is built up by examining the Type that the calling
 * code would like to deserialize and analyzing it. The Mapping is then executed.
 */
sealed abstract class Mapping

/**
 * An IndependentMapping is any mapping that can exist in its own right without depending on some
 * other mapping. This is true of most mappings.
 */
sealed abstract class IndependentMapping extends Mapping

/**
 * A dependent mapping can only appear within certain other mappings.
 */
sealed abstract class DependentMapping extends Mapping

/**
 * The simplest of all mappings: a mapping directly to a value. This will correspond to one of the
 * JSON "primitives": a Number, String, or Boolean.
 */
case class Value(targetType: Type) extends IndependentMapping

/**
 * A dictionary mapping translates into a Map[_, _], and will be used when the calling code
 * asks for something fitting that shape.
 */
case class Dictionary(keyMapping: IndependentMapping, valueMapping: IndependentMapping) extends IndependentMapping

/**
 * A collection mapping is used when any of the standard collection types are called for by the
 * calling code.
 */
case class Collection(targetType: Type, mapping: IndependentMapping) extends IndependentMapping

/**
 * A heterogenous collection mapping, used when the calling code asks for a Tuple.
 */
case class HeteroCollection(targetType: Type, mappings: Seq[IndependentMapping]) extends IndependentMapping

/**
 * A mapping that should instantiate a particular class. This mapping is designed to support
 * classes with multiple constructors.
 */
case class Constructor(targetType: Type, choices: Seq[DeclaredConstructor]) extends IndependentMapping {
  def bestMatching(argNames: Seq[String]): Option[DeclaredConstructor] = {
    val names = Set(argNames: _*)
    def countOptionals(args: List[Argument]) =
      args.foldLeft(0)((n, x) => if (x.optional) n+1 else n)
    def score(args: List[Argument]) =
      args.foldLeft(0)((s, arg) => if (names.contains(arg.path)) s+1 else -100)

    if (choices.isEmpty) {
      None
    } else {
      val best = choices.tail.foldLeft((choices.head, score(choices.head.args))) { (best, c) =>
        val newScore = score(c.args)
        if (newScore == best._2) {
          if (countOptionals(c.args) < countOptionals(best._1.args))
            (c, newScore) else best
        } else if (newScore > best._2) (c, newScore) else best
      }
      Some(best._1)
    }
  }

  lazy val typeInfo = {
    val classSymbol = targetType.typeSymbol.asClass
    val clazz = Class.forName(classSymbol.fullName)

    val typeParams = targetType.typeParams.map { typeParamSymbol =>
      Class.forName(typeParamSymbol.fullName)
    }

    val parameterizedType = if (typeParams.isEmpty) {
      None
    } else {
      Some(new ParameterizedType {
        override def getTypeName() = "generated"
        override def getRawType() = clazz
        override def getOwnerType() = clazz
        override def getActualTypeArguments() = typeParams.toArray
      })
    }

    TypeInfo(clazz, parameterizedType)
  }
}

/**
 * A declared constructor on the type that we would like to instantiate.
 */
case class DeclaredConstructor(constructor: MethodMirror, args: List[Argument])

/**
 * An argument for the declared constructor and the mapping for how to extract that argument.
 */
case class Argument(path: String, mapping: IndependentMapping, optional: Boolean) extends DependentMapping
