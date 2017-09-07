package org.mmisw.cf2rdf

import org.apache.jena.rdf.model.Model
import org.apache.jena.vocabulary._
import org.mmisw.cf2rdf.config.cfg

import scala.xml.Node

/**
 * CF standard names vocabulary to RDF converter.
 *
 * @param xmlIn       Input XML
 */
class Converter(xmlIn: Node) {

  /** some general properties from the input */
  val props: Map[String,String] = {
    val keys = List("version_number", "last_modified") //, "institution", "contact")
    (keys map (k ⇒ k -> (xmlIn \ k).text.trim)).toMap
  }

  /**
   * Does the conversion
   *
   * @return  Resulting Jena model
   */
  def convert: Model = {
    val lastModifiedOpt = props.get("last_modified")

    val namespace = cfg.rdf.iri + "/"

    val M = new ModelConstructor(namespace, lastModifiedOpt)

    val mapper = new OrrNvsMapper(lastModifiedOpt)

    for (entry ← xmlIn \\ "entry") {
      stats.numEntries += 1

      val id = entry.attribute("id").get
      val concept = M.createConcept(namespace + id)

      M.addCanonicalUnits(concept, (entry \ "canonical_units").text.trim)
      M.addDefinition(concept, (entry \ "description").text.trim)

      //concept.addProperty(RDFS.comment, description);

      M.currentTopConcept.addProperty(SKOS.narrower, concept)

      mapper.addOrrTerm(concept)
    }

    stats.mappingTermsAdded = mapper.done()
    stats.mappingOutputFilename = Some(cfg.mapping.filename)

    M.model
  }
}
