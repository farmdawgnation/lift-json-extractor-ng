name := "json-extractor-ng"

version := "0.3.0"

organization := "net.liftmodules"

scalaVersion := "2.12.4"

val liftVersion = settingKey[String]("Lift Web Framework full version number")
val liftEdition = settingKey[String]("Lift Edition (such as 2.6 or 3.0)")

liftVersion := "3.2.0"

liftEdition := (liftVersion apply { _.substring(0,3) }).value

moduleName := name.value + "_" + liftEdition.value

libraryDependencies := Seq(
  "net.liftweb"    %% "lift-json"     % liftVersion.value,
  "org.scala-lang" %  "scala-reflect" % "2.12.4",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

credentials += Credentials(Path.userHome / ".sonatype")

pomExtra := {
  <url>https://github.com/liftmodules/json-extractor-ng</url>
  <licenses>
    <license>
      <name>Apache 2.0 License</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:liftmodules/json-extractor-ng.git</url>
    <connection>scm:git:git@github.com:liftmodules/json-extractor-ng.git</connection>
  </scm>
  <developers>
    <developer>
      <id>liftmodules</id>
      <name>Lift Team</name>
      <url>http://www.liftmodules.net</url>
    </developer>
  </developers>
}
