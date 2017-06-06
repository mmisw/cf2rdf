package org.mmisw.cf2rdf

import org.apache.jena.rdf.model.Model
import org.apache.jena.vocabulary._

import scala.xml.Node

/**
 * CF standard names vocabulary to RDF converter.
 *
 * @param xmlIn       Input XML
 * @param namespace   Namespace for the generated ontology
 * @param mapper      Optional mapper
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
    val M = new ModelConstructor(namespace,
      props.get("version_number"),
      props.get("last_modified")
    )

    for (entry <- xmlIn \\ "entry") {
      stats.numEntries += 1

      val id = entry.attribute("id").get
      val concept = M.createConcept(namespace + id)

      M.addCanonicalUnits(concept, (entry \ "canonical_units").text.trim)
      M.addDefinition(concept, (entry \ "description").text.trim)

      //concept.addProperty(RDFS.comment, description);

      M.currentTopConcept.addProperty(SKOS.narrower, concept)

      mapper.foreach(_.addOrrTerm(concept))
    }

    mapper foreach { mapper =>
      val (t, f) = mapper.done()
      stats.mappingTermsAdded = t
      stats.mappingOutputFilename = Some(f)
    }

    M.model
  }
}
