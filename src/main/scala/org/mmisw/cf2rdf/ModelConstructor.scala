package org.mmisw.cf2rdf

import org.apache.jena.ontology.OntModelSpec
import org.apache.jena.rdf.model.{ModelFactory, Property, Resource}
import org.apache.jena.vocabulary._

class ModelConstructor(namespace: String) {
  // to capture some "original vocabulary" metadata, in particular, version_number and
  // last_modified (in omvmmi:origVocVersionId and omvmmi:origVocLastModified, resp).
  // (see https://marinemetadata.org/community/teams/ont/mmirepository/communityontmetadata;
  // origVocLastModified actually just introduced in this update of the converter.)
  val omvmmi = "http://mmisw.org/ont/mmi/20081020/ontologyMetadata/"

  val model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM)
  model.setNsPrefix("", namespace)
  model.setNsPrefix("skos", SKOS.uri)
  model.setNsPrefix("omvmmi", omvmmi)
  model.createResource(SKOS.uri + "Concept", RDFS.Class)

  val origVocVersionId    = model.createProperty(omvmmi + "origVocVersionId")
  val origVocLastModified = model.createProperty(omvmmi + "origVocLastModified")

  val standardNameClass: Resource = model.createResource(namespace + "Standard_Name")
  val currentTopConcept = createConcept(namespace + "parameter")
  val canonical_units = model.createProperty(namespace + "canonical_units")

  model.add(model.createStatement(standardNameClass, RDF.`type`, OWL.Class))
  model.add(model.createStatement(standardNameClass, RDFS.subClassOf, SKOS.Concept))
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
      concept.addProperty(SKOS.definition, definition)
    }
    else {
      stats.numWithNoDefinitions += 1
    }
  }

}
