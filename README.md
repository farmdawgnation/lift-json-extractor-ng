# json-extractor-ng

This project is a new extractor for lift-json based on Scala reflection instead of
Java reflection. Currently, it requires Scala 2.12 and only works against Lift 3.1.

To use it, add it to your library dependencies like so:

```scala
libraryDependencies += "net.liftmodules" %% "json-extrator-ng_3.1" % "0.1.0"
```

Once it's set up, import the `Extraction` implicit conversions and use the
`extractNg` method to extract using this extractor instead of the default Lift
extraction.

```scala
import net.liftmodules.jsonextractorng.Extraction._

myJValue.extract[Thing]
```
