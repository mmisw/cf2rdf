package org.mmisw.cf2rdf
import scala.xml.Node

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.vocabulary.OWL
import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS
import com.hp.hpl.jena.vocabulary.XSD
import java.io.PrintWriter


/**
 * CF standard names vocabulary to RDF converter.
 *
 * @author Carlos Rueda
 */
object cf2rdf extends App {

  import scala.collection.mutable

  /**
   * helper to process arguments and run program.
   * @param opts
   * @param block
   * @return
   */
  def withOptions(opts: mutable.Map[String, String])(block : => Unit) {
    val defaults = {
      val sep = "\n    "
      sep + (for ((k,v) <- opts.toMap) yield s"$k = $v").mkString(sep)
    }
    val usage =
      s"""
        | USAGE:
        |   cf2rdf --xml xml [--rdf xml] [--namespace namespace]
        | Example:
        |   cf2rdf --xml src/main/resources/cf-standard-name-table-25.xml
        |   generates src/main/resources/cf-standard-name-table-25.rdf
        |
        | Defaults: $defaults
        |
      """.stripMargin

    next(args.toList)

    def next(list: List[String]) {
      list match {
        case "--xml" :: xml :: args => {opts("xml") = xml; next(args)}
        case "--rdf" :: rdf :: args => {opts("rdf") = rdf; next(args)}
        case "--namespace" :: namespace :: args => {opts("namespace") = namespace; next(args)}
        case Nil => if (opts.contains("xml")) block else println(usage)
        case _ => println(usage)
      }
    }
  }

  val opts = mutable.Map[String, String]()
  opts("namespace")  = "http://mmisw.org/ont/cf/parameter/"

  withOptions(opts) {
    val xmlFilename = opts("xml")
    val rdfFilename = opts.getOrElse("rdf", xmlFilename.replaceAll("\\.xml$", ".rdf"))
    val statsFilename = xmlFilename.replaceAll("\\.xml$", ".conv-stats.txt")
    val namespace   = opts("namespace")

    val xmlIn = scala.xml.XML.loadFile(xmlFilename)
    val converter = new Converter(xmlIn, namespace)
    val model = converter.convert

    saveModel()
    writeStats()

    println(s"""generated: $rdfFilename
               |            $statsFilename
               """.stripMargin)

    def saveModel() {
      val writer = model.getWriter("RDF/XML-ABBREV")
      writer.setProperty("showXmlDeclaration", "true")
      writer.setProperty("relativeURIs", "same-document,relative")
      writer.setProperty("xmlbase", namespace)

      // model.setNsPrefix("",NS);
      // model.write(fo,"RDF/XML-ABBREV");

      writer.write(model, new java.io.FileOutputStream(rdfFilename), null)
    }

    def writeStats() {
      val pw = new PrintWriter(statsFilename)
      pw.printf(
        s"""
           | cf2rdf conversion
           | date:   ${new java.util.Date()}
           | input:  $xmlFilename
           | output: $rdfFilename
           | ${converter.stats}
        """.stripMargin)
      pw.close()
    }
  }
}

/**
 * The converter.
 *
 * note: the conversion is identical to the original one in the "watchdog" project.
 * @todo general revision
 *
 * @param xmlIn       Input XML
 * @param namespace   Namespace for the generated ontology
 */
class Converter(xmlIn: Node, namespace: String) {

  private val model = Skos.createModel
  private val standardNameClass: Resource = model.createResource(namespace + "Standard_Name")
  private val currentTopConcept = _createConcept(namespace + "parameter")
  private val canonical_units = model.createProperty(namespace + "canonical_units")

  model.setNsPrefix("", namespace)
  model.add(model.createStatement(standardNameClass, RDF.`type`, OWL.Class))
  model.add(model.createStatement(standardNameClass, RDFS.subClassOf, Skos.Concept))
  model.add(model.createStatement(standardNameClass, RDFS.label, "Standard Name"))
  model.add(model.createStatement(canonical_units, RDF.`type`, OWL.DatatypeProperty))
  model.add(model.createStatement(canonical_units, RDFS.domain, standardNameClass))
  model.add(model.createStatement(canonical_units, RDFS.range, XSD.xstring))

  object stats {
    var numConcepts = 0
    var numEntries = 0
    var numWithNoCanonicalUnits = 0
    var numWithNoDefinitions = 0

    override def toString =
      s"""
        |  numConcepts = $numConcepts
        |  numEntries = $numEntries
        |  numWithNoCanonicalUnits = $numWithNoCanonicalUnits
        |  numWithNoDefinitions = $numWithNoDefinitions
      """.stripMargin

  }

  /**
   * Does the conversion
   *
   * @return  Resulting Jena model
   */
  def convert: Model = {

    // todo: generate something for the general properties of the file:
//    val props = {
//      val keys = List("version_number", "last_modified", "institution", "contact")
//      keys foreach {k =>
//        // ...
//      }
//    }

    for (entry <- xmlIn \\ "entry") {
      stats.numEntries += 1

      val id = entry.attribute("id").get
      val concept = _createConcept(namespace + id)

      val canonicalUnits = (entry \ "canonical_units").text.trim
      val description    = (entry \ "description").text.trim

      if ( canonicalUnits.length > 0 ) {
        concept.addProperty(canonical_units, canonicalUnits)
      }
      else {
        stats.numWithNoCanonicalUnits += 1
      }

      if ( description.length > 0 ) {
        concept.addProperty(Skos.definition, description)
      }
      else {
        stats.numWithNoDefinitions += 1
      }

      //concept.addProperty(RDFS.comment, description);

      currentTopConcept.addProperty(Skos.narrower, concept)

    }

    model
  }

  def _createConcept(uri: String): Resource = {
    val concept = model.createResource(uri, standardNameClass)
    stats.numConcepts += 1
    concept
  }
}
