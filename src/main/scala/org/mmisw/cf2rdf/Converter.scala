package org.mmisw.cf2rdf

import org.apache.jena.ontology.OntModelSpec
import org.apache.jena.rdf.model.{Model, Property, Resource, ModelFactory}
import org.apache.jena.vocabulary.{XSD, OWL, RDF, RDFS}

import scala.xml.Node

/**
 * CF standard names vocabulary to RDF converter.
 *
 * @param xmlIn       Input XML
 * @param namespace   Namespace for the generated ontology
 * @param mapper
 */
class Converter(xmlIn: Node, namespace: String, mapper: Option[OrrNvsMapper]) {

  /** some general properties from the input */
  val props: Map[String,String] = {
    val keys = List("version_number", "last_modified") //, "institution", "contact")
    (keys map (k => k -> (xmlIn \ k).text.trim)).toMap
  }

  /**
   * Does the conversion
   *
   * @return  Resulting Jena model
   */
  def convert: Model = {
    M.addVersionNumber(props.getOrElse("version_number", ""))
    M.addLastModified(props.getOrElse("last_modified", ""))

    for (entry <- xmlIn \\ "entry") {
      stats.numEntries += 1

      val id = entry.attribute("id").get
      val concept = M.createConcept(namespace + id)

      M.addCanonicalUnits(concept, (entry \ "canonical_units").text.trim)
      M.addDefinition(concept, (entry \ "description").text.trim)

      //concept.addProperty(RDFS.comment, description);

      M.currentTopConcept.addProperty(Skos.narrower, concept)

      mapper.foreach(_.addOrrTerm(concept))
    }

    mapper.foreach { mapper =>
      val (t, f) = mapper.done()
      stats.mappingTermsAdded = t
      stats.mappingOutputFilename = Some(f)
    }

    M.model
  }

  /** model construction helper */
  private object M {

    // to capture some "original vocabulary" metadata, in particular, version_number and
    // last_modified (in omvmmi:origVocVersionId and omvmmi:origVocLastModified, resp).
    // (see https://marinemetadata.org/community/teams/ont/mmirepository/communityontmetadata;
    // origVocLastModified actually just introduced in this update of the converter.)
    val omvmmi = "http://mmisw.org/ont/mmi/20081020/ontologyMetadata/"

    val model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM)
    model.setNsPrefix("", namespace)
    model.setNsPrefix("skos", Skos.NS)
    model.setNsPrefix("omvmmi", omvmmi)
    model.createResource(Skos.NS + "Concept", RDFS.Class)

    val origVocVersionId    = model.createProperty(omvmmi + "origVocVersionId")
    val origVocLastModified = model.createProperty(omvmmi + "origVocLastModified")

    val standardNameClass: Resource = model.createResource(namespace + "Standard_Name")
    val currentTopConcept = createConcept(namespace + "parameter")
    val canonical_units = model.createProperty(namespace + "canonical_units")

    model.add(model.createStatement(standardNameClass, RDF.`type`, OWL.Class))
    model.add(model.createStatement(standardNameClass, RDFS.subClassOf, Skos.Concept))
    model.add(model.createStatement(standardNameClass, RDFS.label, "Standard Name"))
    model.add(model.createStatement(canonical_units, RDF.`type`, OWL.DatatypeProperty))
    model.add(model.createStatement(canonical_units, RDFS.domain, standardNameClass))
    model.add(model.createStatement(canonical_units, RDFS.range, XSD.xstring))

    val ontology = model.createOntology("")

    def addStringProperty(property: Property, value: String) {
      if (value.trim.length > 0) {
        ontology.addProperty(property, value.trim)
      }
    }

    def addVersionNumber(version_number: String) {
      addStringProperty(origVocVersionId, version_number)
    }

    def addLastModified(last_modified: String) {
      addStringProperty(origVocLastModified, last_modified)
    }

    def createConcept(uri: String): Resource = {
      val concept = model.createResource(uri, standardNameClass)
      stats.numConcepts += 1
      concept
    }

    def addCanonicalUnits(concept: Resource, canonicalUnits: String) {
      if ( canonicalUnits.length > 0 ) {
        concept.addProperty(canonical_units, canonicalUnits)
      }
      else {
        stats.numWithNoCanonicalUnits += 1
      }
    }

    def addDefinition(concept: Resource, definition: String) {
      if ( definition.length > 0 ) {
        concept.addProperty(Skos.definition, definition)
      }
      else {
        stats.numWithNoDefinitions += 1
      }
    }
  }

  object stats {
    var numConcepts = 0
    var numEntries = 0
    var numWithNoCanonicalUnits = 0
    var numWithNoDefinitions = 0

    var mappingTermsAdded = 0
    var mappingOutputFilename: Option[String] = None

    override def toString =
      s"""numConcepts = $numConcepts
         |numEntries = $numEntries
         |numWithNoCanonicalUnits = $numWithNoCanonicalUnits
         |numWithNoDefinitions = $numWithNoDefinitions
         |
         |Mapping ontology:
         |  mappingTermsAdded     = $mappingTermsAdded
         |  mappingOutputFilename = ${mappingOutputFilename.get}
       """.stripMargin
  }

}
