package net.liftmodules.jsonextractorng

import org.scalatest._
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import scala.reflect.runtime.{universe=>ru}
  import ru._

class ExtractionNgSpec extends FlatSpec with Matchers {
  import Extraction._

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
}

case class SimpleCaseClass(name: String, age: Int)
