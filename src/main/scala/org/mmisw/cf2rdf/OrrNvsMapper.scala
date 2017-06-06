package org.mmisw.cf2rdf

import java.io.{ByteArrayInputStream, FileInputStream, StringReader}
import java.nio.charset.StandardCharsets

import org.apache.jena.ontology.OntModelSpec
import org.apache.jena.rdf.model.{ModelFactory, Resource}
import org.apache.jena.vocabulary.SKOS

import scala.collection.JavaConversions._


class OrrNvsMapper(nvsFilename: String) {

  private val mapNamespace   = "http://mmisw.org/ont/mmi/cfonmap/"
  private val orrCfNamespace = "http://mmisw.org/ont/cf/parameter/"
  private val nvsCfNamespace = "http://vocab.nerc.ac.uk/collection/P07/current/"

  private val model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM)

  model.setNsPrefix("",    mapNamespace)
  model.setNsPrefix("orr", orrCfNamespace)
  model.setNsPrefix("nvs", nvsCfNamespace)

  model.setNsPrefix("skos", SKOS.uri)

  private val map = loadNvs()

  private var termsAdded = 0

  def addOrrTerm(orrTerm: Resource): Unit = {
    val cfName: String = orrTerm.getLocalName
    for (nvsUri <- map.get(cfName)) {
      val nvsTerm: Resource = model.createResource(nvsUri)
      model.add(model.createStatement(orrTerm, SKOS.exactMatch, nvsTerm))
      termsAdded += 1
    }
  }

  def done(): (Int, String) = {
    val outFilename = "src/main/resources/cfonmap.n3"
    val out = new java.io.FileOutputStream(outFilename)
    model.getWriter("N3").write(model, out, null)
    (termsAdded, outFilename)
  }


  private def loadNvs(): Map[String, String] = {

    val url = "http://vocab.nerc.ac.uk/collection/P07/current/"

    val nvsModel = ModelFactory.createDefaultModel()

    val collection = nvsModel.createResource("http://vocab.nerc.ac.uk/collection/P07/current/")
    val member = nvsModel.createProperty("http://www.w3.org/2004/02/skos/core#member")
    val prefLabel = nvsModel.createProperty("http://www.w3.org/2004/02/skos/core#prefLabel")

    //println(s"Loading $nvsFilename")
    val originalInputStream = new FileInputStream(nvsFilename)

    val replaceSpaces = true

    if (replaceSpaces) {
      val fixedLines = scala.collection.mutable.ListBuffer[String]()
      val re = """(.*)rdf:resource="(.*) (.*)"(.*)""".r
      val src = io.Source.fromInputStream(originalInputStream, "utf8")
      val fixed: String = (src.getLines() map {
        case line@re(p1, p2, p3, p4) ⇒
          fixedLines += line
          s"""${p1}rdf:resource="$p2%20$p3"$p4"""
        case line ⇒ line
      }).mkString("\n")

      if (fixedLines.nonEmpty) {
          println(
            s"""
               |Replaced %20 for space in the following rdf:resource IRIs from $nvsFilename
               |${fixedLines.mkString("\t", "\n\t", "")}
             """.stripMargin)
      }
      nvsModel.read(new ByteArrayInputStream(fixed.getBytes(StandardCharsets.UTF_8)), url, "RDF/XML")
    }
    else {
      nvsModel.read(originalInputStream , url, "RDF/XML")
    }
    //println(s"done loading.")

    val members = nvsModel.listObjectsOfProperty(collection, member)

    var map = Map[String, String]()

    var numMembers = 0
    members foreach { member =>
      numMembers += 1
      //println(s"$member")

      val resource = member.asInstanceOf[Resource]
      val prefLabels = nvsModel.listObjectsOfProperty(resource, prefLabel)
      prefLabels foreach { labelNode =>
        if (labelNode.isLiteral) {
          val literal = labelNode.asLiteral
          val lexicalForm = literal.getLexicalForm
          //println(s"  literal.getLexicalForm: $lexicalForm")
          map = map.updated(lexicalForm, resource.getURI)
        }
        else {
          println(s"  unexpected: $labelNode")
        }
      }
    }
    map
  }
}
