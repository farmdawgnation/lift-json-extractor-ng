name := "json-extractor-ng_3.1"

version := "0.0.1-SNAPSHOT"

organization := "net.liftmodules"

scalaVersion := "2.12.2"

libraryDependencies := Seq(
  "net.liftweb"    %% "lift-json"     % "3.1.0",
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
