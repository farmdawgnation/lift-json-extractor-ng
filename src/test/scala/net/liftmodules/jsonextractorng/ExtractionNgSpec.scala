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
}

case class SimpleCaseClass(name: String, age: Int)
