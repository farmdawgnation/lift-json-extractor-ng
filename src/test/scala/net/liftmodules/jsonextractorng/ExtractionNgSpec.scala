package net.liftmodules.jsonextractorng

import org.scalatest._
import net.liftweb.json.{Formats, Serializer, DefaultFormats, TypeInfo}
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import scala.reflect.runtime.{universe=>ru}
  import ru._

class ExtractionNgSpec extends FlatSpec with Matchers {
  import Extraction._

  implicit val formats = DefaultFormats

  "ExtractionNg" should "correctly extract native things" in {
    JInt(5).extractNg[Int] should equal(5)
    JBool(true).extractNg[Boolean] should equal(true)
    JString("happy days").extractNg[String] should equal("happy days")
  }

  it should "handle simple case classes defined at root" in {
    val input: JObject = ("name" -> "Burt") ~ ("age" -> 40)

    input.extractNg[SimpleCaseClass] should equal(SimpleCaseClass("Burt", 40))
  }

  it should "correctly extract a map from an object" in {
    val input: JObject = ("name" -> "Burt") ~ ("occupation" -> "Software Engineer")

    input.extractNg[Map[String, String]] should equal(Map("name" -> "Burt", "occupation" -> "Software Engineer"))
  }

  it should "correctly extract a list from a JArray" in {
    val input: JArray = JArray(List(JString("thing one"), JString("thing two"), JString("thing three")))
    val expectedOutput = List("thing one", "thing two", "thing three")

    val output = input.extractNg[List[String]]

    output should equal(expectedOutput)
  }

  ignore should "correctly extract an array from a JArray" in {
    val input: JArray = JArray(List(JString("thing one"), JString("thing two"), JString("thing three")))
    val expectedOutput = Array("thing one", "thing two", "thing three")

    val output = input.extractNg[Array[String]]

    output should equal(expectedOutput)
  }

  it should "correctly extract a set from a JArray" in {
    val input: JArray = JArray(List(JString("thing one"), JString("thing two"), JString("thing one")))
    val expectedOutput = Set("thing one", "thing two")

    val output = input.extractNg[Set[String]]

    output should equal(expectedOutput)
  }

  it should "correctly handle Option presence" in {
    val input: JString = JString("bacon")
    val expectedOutput = Some("bacon")

    val output = input.extractNg[Option[String]]

    output should equal(expectedOutput)
  }

  it should "correctly handle Option absence" in {
    val input = JNull
    val expectedOutput = None

    val output = input.extractNg[Option[String]]

    output should equal(expectedOutput)
  }

  it should "correctly extract a Tuple" in {
    val input = JArray(List(JString("bacon"), JInt(2)))
    val expectedOutput = ("bacon", 2)

    val output = input.extractNg[Tuple2[String, Int]]

    output should equal(expectedOutput)
  }

  it should "properly handle a custom deserializer without a param type" in {
    val customDeserializer = new Serializer[Baconizer] {
      val clazz = classOf[Baconizer]
      override def serialize(implicit formats: Formats) = ???

      override def deserialize(implicit formats: Formats) = {
        case (TypeInfo(`clazz`, None), json) =>
          val name = (json \ "name").extractNg[String]
          Baconizer(name, 3.14)
      }
    }

    implicit val formats = DefaultFormats + customDeserializer
    val input: JObject = ("name" -> "Testy McTestface")
    val output = input.extractNg[Baconizer]

    output should equal(Baconizer("Testy McTestface", 3.14))
  }

  it should "properly handle a custom deserializer with a param type" in {
    val customDeserializer = new Serializer[Baconizer2[_]] {
      val clazz = classOf[Baconizer2[_]]
      override def serialize(implicit formats: Formats) = ???

      override def deserialize(implicit formats: Formats) = {
        case (TypeInfo(`clazz`, paramType), json) =>
          val name = (json \ "name").extractNg[String]
          Baconizer2[String](name, 3.14)
      }
    }

    implicit val formats = DefaultFormats + customDeserializer
    val input: JObject = ("name" -> "Testy McTestface")
    val output = input.extractNg[Baconizer2[String]]

    output should equal(Baconizer2[String]("Testy McTestface", 3.14))
  }
}

case class SimpleCaseClass(name: String, age: Int)
case class Baconizer(name: String, pi: Double)
case class Baconizer2[T](name: String, pi: Double)
