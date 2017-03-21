package net.liftmodules.jsonextractorng

import net.liftweb.json._
import scala.reflect.runtime.{universe=>ru}

object Extraction {
  implicit class ExtractionNg(underlyingJValue: JValue) {
    def extractNg[RequestedType](implicit typeTag: ru.TypeTag[RequestedType]): RequestedType = {
      ???
    }
  }
}
