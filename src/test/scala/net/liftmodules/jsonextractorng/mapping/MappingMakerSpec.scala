package net.liftmodules.jsonextractorng.mapping

import org.scalatest._
import scala.reflect.runtime.{universe=>ru}
import ru._

class MappingMakerSpec extends FlatSpec with Matchers {
  val testRuntimeMirror = runtimeMirror(getClass.getClassLoader)

  import Constants._
  "MappingMaker" should "correctly identify primitives" in {
    primitiveTypes.map { primType =>
      MappingMaker.makeMapping(primType) should equal(Value(primType))
    }
  }

  it should "correctly identify collections" in {
    val suts = Seq(
      typeOf[List[String]],
      typeOf[Option[String]],
      typeOf[Set[String]],
      typeOf[Array[String]]
    ).map { baseType =>
      (baseType, baseType.typeConstructor, baseType.typeArgs(0))
    }

    suts.map {
      case (baseType, typeConstructor, parameterizedArgument) =>
        MappingMaker.makeMapping(baseType) should equal(Collection(typeConstructor, Value(parameterizedArgument)))
    }
  }

  it should "correctly identify hereo collections" in {
    val tup = typeOf[Tuple2[String, Int]]
    val mapping = MappingMaker.makeMapping(tup)
    val expectedMapping = HeteroCollection(
      tup.typeConstructor,
      Seq(Value(typeOf[String]), Value(typeOf[Int]))
    )

    mapping should equal(expectedMapping)
  }

  it should "correctly identifiy dictionaries" in {
    val suts = Seq(
      typeOf[Map[String, String]]
    ).map { baseType =>
      (baseType, baseType.typeConstructor, baseType.typeArgs(0), baseType.typeArgs(1))
    }

    suts.map {
      case (baseType, typeConstructor, keyType, valueType) =>
        MappingMaker.makeMapping(baseType) should equal(Dictionary(Value(keyType), Value(valueType)))
    }
  }

  it should "correctly identify objects" in {
    val ctorSymbol = typeOf[ExampleClazz].decl(ru.termNames.CONSTRUCTOR).asTerm.alternatives.map(_.asMethod).toList(0)
    val targetClass = typeOf[ExampleClazz].typeSymbol.asClass
    val classMirror = testRuntimeMirror.reflectClass(targetClass)
    val ctor = classMirror.reflectConstructor(ctorSymbol)

    val resultMappingToStr = MappingMaker.makeMapping(typeOf[ExampleClazz]).toString
    val expectedMappingToStr = Constructor(
      typeOf[ExampleClazz],
      List(
        DeclaredConstructor(
          ctor,
          List(
            Argument("name", Value(typeOf[String]), false),
            Argument("favoriteColorIsBlue", Value(typeOf[Boolean]), false),
            Argument("bonusData", Collection(typeOf[Option[_]].typeConstructor, Value(typeOf[String])), true)
          )
        )
      )
    ).toString

    // This is lame, but it looks like no to reflections of the constructor are equal and
    // as a result we need to compare the string representation of them.
    resultMappingToStr should equal(expectedMappingToStr)
  }
}

case class ExampleClazz(name: String, favoriteColorIsBlue: Boolean, bonusData: Option[String])
