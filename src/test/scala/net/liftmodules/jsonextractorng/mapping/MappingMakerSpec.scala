package net.liftmodules.jsonextractorng.mapping

import org.scalatest._
import scala.reflect.runtime.{universe=>ru}
import ru._

class MappingMakerSpec extends FlatSpec with Matchers {
  "MappingMaker" should "correctly identify primitives" in {
    MappingMaker.primitiveTypes.map { primType =>
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
    MappingMaker.makeMapping(typeOf[ExampleClazz]) should equal(Constructor(
      typeOf[ExampleClazz],
      List(
        DeclaredConstructor(
          typeOf[ExampleClazz].decl(ru.termNames.CONSTRUCTOR).asTerm.alternatives.map(_.asMethod).toList(0),
          List(
            Argument("name", Value(typeOf[String]), false),
            Argument("favoriteColorIsBlue", Value(typeOf[Boolean]), false),
            Argument("bonusData", Collection(typeOf[Option[_]].typeConstructor, Value(typeOf[String])), true)
          )
        )
      )
    ))
  }
}

case class ExampleClazz(name: String, favoriteColorIsBlue: Boolean, bonusData: Option[String])
