package org.mmisw.cf2rdf


object stats {
  var numConcepts = 0
  var numEntries = 0
  var numWithNoCanonicalUnits = 0
  var numWithNoDefinitions = 0

  var mappingTermsAdded = 0
  var mappingOutputFilename: Option[String] = None

  override def toString: String =
    s"""numConcepts = $numConcepts
       |numEntries = $numEntries
       |numWithNoCanonicalUnits = $numWithNoCanonicalUnits
       |numWithNoDefinitions = $numWithNoDefinitions
       |
       |Mapping ontology:
       |  mappingTermsAdded     = $mappingTermsAdded
       |  mappingOutputFilename = ${mappingOutputFilename.getOrElse("(not generated)")}
       """.stripMargin

}
