lazy val cf2rdfVersion = setVersion("0.3.0")
val scalaV             = "2.12.2"
val cfgV               = "0.0.7"
val scalajHttpV        = "2.3.0"
val typesafeConfigV    = "1.2.1"
val scalametaParadiseV = "3.0.0-M8"
val scalaXmlV          = "1.0.6"
val jenaV              = "3.3.0"
val slf4jSimpleV       = "1.7.25"

name := "cf2rdf"
organization := "org.mmisw"
version := cf2rdfVersion
scalaVersion := scalaV

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-language:_")

libraryDependencies ++= Seq(
  "org.scala-lang.modules"  %%  "scala-xml"       % scalaXmlV,
  "com.github.carueda"      %%  "cfg"             % cfgV % "provided",
  "org.scalaj"              %%  "scalaj-http"     % scalajHttpV,
  "com.typesafe"             %  "config"          % typesafeConfigV,
  "org.apache.jena"          %  "jena"            % jenaV,
  "org.apache.jena"          %  "jena-tdb"        % jenaV, //(*)
  "org.slf4j"                %  "slf4j-simple"    % slf4jSimpleV

  //(*) https://jena.apache.org/download/maven.html:
  //  "...use of <type>pom</type> ... does not work in all tools.
  //  An alternative is to depend on jena-tdb, which will pull in the other artifacts."
)

addCompilerPlugin(
  ("org.scalameta" % "paradise" % scalametaParadiseV).cross(CrossVersion.full)
)

def setVersion(version: String): String = {
  println(s"cf2rdf $version")
  IO.write(file("src/main/resources/reference.conf"), s"cf2rdf.version = $version")
  version
}
