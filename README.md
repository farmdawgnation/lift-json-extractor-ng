# json-extractor-ng

This project is a new extractor for lift-json based on Scala reflection instead of
Java reflection. Currently, it requires Scala 2.12 and only works against Lift 3.1 or 3.2.

To use it, you'll just need to add a line to your build file. For the Lift 3.1 edition simply
add:

```scala
libraryDependencies += "net.liftmodules" %% "json-extractor-ng_3.1" % "0.3.0"
```

For the Lift 3.2 edition:

```scala
libraryDependencies += "net.liftmodules" %% "json-extractor-ng_3.2" % "0.3.0"
```

Once it's set up, import the `Extraction` implicit conversions and use the
`extractNg` method to extract using this extractor instead of the default Lift
extraction.

```scala
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftmodules.jsonextractorng.Extraction._

case class Thing(name: String, age: Int)

implicit val formats = DefaultFormats
val myJValue = ("name" -> "Bob") ~ ("age" -> 500)

myJValue.extractNg[Thing] // => Thing("Bob", 500)
```

## Known Issues

None (yet)
