import sbt._
import Keys._

object ChimeraBuild extends Build {
  val defaultSettings = Defaults.defaultSettings ++ Seq(
    version := "0.1.0",
    organization := "com.github.philcali",
    scalaVersion := "2.10.0",
    scalacOptions ++= Seq("-feature", "-language:dynamics", "-language:implicitConversions"),
    libraryDependencies <+= (scalaVersion) {
      "org.scala-lang" % "scala-reflect" % _
    }
  )

  lazy val library = Project(
    "chimera-library",
    file("library"),
    settings = defaultSettings
  )

  lazy val json = Project(
    "chimera-json",
    file("json"),
    settings = defaultSettings
  ) dependsOn library

  lazy val xml = Project(
    "chimera-xml",
    file("xml"),
    settings = defaultSettings
  ) dependsOn library

  lazy val root = Project(
    "chimera",
    file("."),
    settings = defaultSettings
  ) aggregate (library, json, xml)
}
