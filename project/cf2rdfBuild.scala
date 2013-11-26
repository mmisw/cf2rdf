import sbt._
import sbt.Keys._

object cf2rdfBuild extends Build {

  lazy val cf2rdf = Project(
    id = "cf2rdf",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "cf2rdf",
      organization := "org.mmisw",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.2",
      libraryDependencies ++= Seq(
        "com.hp.hpl.jena"   % "jena"    % "2.6.3"
      )
    )
  )
}
