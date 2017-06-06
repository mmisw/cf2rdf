name := "cf2rdf"
organization := "org.mmisw"
version := "0.2.0"
scalaVersion := "2.11.8"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-language:_")

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %%   "scala-xml"                 % "1.0.6",
  "com.typesafe"            %   "config"                    % "1.2.1",
  "org.apache.jena"         %   "jena"                      % "3.3.0",
  "org.apache.jena"         %   "jena-tdb"                  % "3.3.0"
    //(*) https://jena.apache.org/download/maven.html:
    //  "...use of <type>pom</type> ... does not work in all tools.
    //  An alternative is to depend on jena-tdb, which will pull in the other artifacts."
)

