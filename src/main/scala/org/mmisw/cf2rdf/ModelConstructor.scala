package org.mmisw.cf2rdf

import org.apache.jena.ontology.{OntModel, OntModelSpec}
import org.apache.jena.rdf.model.{ModelFactory, Property, Resource}
import org.apache.jena.vocabulary._
import org.mmisw.orr.ont.vocabulary.{Omv, OmvMmi}

class ModelConstructor(namespace: String,
                       versionNumberOpt: Option[String],
                       lastModifiedOpt: Option[String]
                      ) {

  val model: OntModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM)

  model.setNsPrefix("", namespace)
  model.setNsPrefix("skos", SKOS.uri)
  model.setNsPrefix("omv", Omv.NS)
  model.setNsPrefix("omvm", OmvMmi.NS)

  model.createResource(SKOS.uri + "Concept", RDFS.Class)

  private val standardNameClass: Resource = model.createResource(namespace + "Standard_Name")
  val currentTopConcept: Resource = createConcept(namespace + "parameter")
  private val canonical_units = model.createProperty(namespace + "canonical_units")

  model.add(model.createStatement(standardNameClass, RDF.`type`, OWL.Class))
  model.add(model.createStatement(standardNameClass, RDFS.subClassOf, SKOS.Concept))
  model.add(model.createStatement(standardNameClass, RDFS.label, "Standard Name"))
  model.add(model.createStatement(canonical_units, RDF.`type`, OWL.DatatypeProperty))
  model.add(model.createStatement(canonical_units, RDFS.domain, standardNameClass))
  model.add(model.createStatement(canonical_units, RDFS.range, XSD.xstring))

  private val ontology = model.createOntology("http://mmisw.org/ont/cf/parameter")

  addStringProperty(Omv.name, "Climate and Forecast (CF) Standard Names" +
    (if (versionNumberOpt.isDefined) s" (v.${versionNumberOpt.get})" else ""))

  addStringProperty(Omv.description,
    "Ontology representation of the Climate and Forecast (CF) standard names parameter vocabulary," +
      " which is intended for use with climate and forecast data in the atmosphere, surface and ocean domains." +
      " Every CF parameter is captured as a SKOS concept.")

  addStringProperty(Omv.hasCreator, "MMI")

  addStringProperty(Omv.keywords, {
    List(
      "NetCDF", "CF", "Climate and Forecast", "self-describing", "standard names", "Canonical Units"
    ).mkString(", ")
  })

  addStringProperty(Omv.documentation, "http://cfconventions.org/standard-names.html")
  addStringProperty(Omv.hasContributor, "http://cfconventions.org/Data/cf-standard-names/docs/standard-name-contributors.html")
  addStringProperty(Omv.reference, "http://marinemetadata.org/orrcf")

  addStringProperty(Omv.acronym, "CF-standard-name")

  lastModifiedOpt foreach { lm ⇒
    addStringProperty(OmvMmi.origVocLastModified, lm)
    addStringProperty(Omv.creationDate, lm)
  }

  versionNumberOpt foreach { vn ⇒
    addStringProperty(OmvMmi.origVocVersionId, vn)
    addStringProperty(OmvMmi.origVocUri, {
      s"https://raw.githubusercontent.com/cf-convention/cf-convention.github.io/master/Data/cf-standard-names/$vn/src/cf-standard-name-table.xml"
    })
  }

  addStringProperty(OmvMmi.hasResourceType, "http://mmisw.org/ont/mmi/resourcetype/parameter")
  addStringProperty(OmvMmi.hasContentCreator, "CF Metadata")
  addStringProperty(OmvMmi.origMaintainerCode, "cf")

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

  private def addStringProperty(property: Property, value: String) {
    if (value.trim.length > 0) {
      ontology.addProperty(property, value.trim)
    }
  }
}