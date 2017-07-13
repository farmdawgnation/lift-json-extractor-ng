name := "json-extractor-ng_3.1"

version := "0.1.0"

organization := "net.liftmodules"

scalaVersion := "2.12.2"

val liftVersion = settingKey[String]("Lift Web Framework full version number")
val liftEdition = settingKey[String]("Lift Edition (such as 2.6 or 3.0)")

liftVersion := "3.1.0"

liftEdition := (liftVersion apply { _.substring(0,3) }).value

moduleName := name.value + "_" + liftEdition.value

libraryDependencies := Seq(
  "net.liftweb"    %% "lift-json"     % liftVersion.value,
  "org.scala-lang" %  "scala-reflect" % "2.12.2",
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
