package org.mmisw.cf2rdf

import java.io.FileInputStream

import com.hp.hpl.jena.ontology.OntModelSpec
import com.hp.hpl.jena.rdf.model.{ModelFactory, Resource}

import scala.collection.JavaConversions._


class OrrNvsMapper(nvsFilename: String) {

  val mapNamespace   = "http://mmisw.org/ont/mmi/cfonmap/"
  val orrCfNamespace = "http://mmisw.org/ont/cf/parameter/"
  val nvsCfNamespace = "http://vocab.nerc.ac.uk/collection/P07/current/"

  val model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM)

  model.setNsPrefix("",    mapNamespace)
  model.setNsPrefix("orr", orrCfNamespace)
  model.setNsPrefix("nvs", nvsCfNamespace)

  model.setNsPrefix("skos", Skos.NS)

  val map = loadNvs()

  var termsAdded = 0

  def addOrrTerm(orrTerm: Resource) {
    val cfName: String = orrTerm.getLocalName
    for (nvsUri <- map.get(cfName)) {
      val nvsTerm: Resource = model.createResource(nvsUri)
      model.add(model.createStatement(orrTerm, Skos.exactMatch, nvsTerm))
      termsAdded += 1
    }
  }

  def done() = {
    val outFilename = "src/main/resources/cfonmap.n3"
    val out = new java.io.FileOutputStream(outFilename)
    model.getWriter("N3").write(model, out, null)
    (termsAdded, outFilename)
  }


  private def loadNvs() = {

    val url = "http://vocab.nerc.ac.uk/collection/P07/current/"

    val nvsModel = ModelFactory.createDefaultModel()

    val collection = nvsModel.createResource("http://vocab.nerc.ac.uk/collection/P07/current/")
    val member = nvsModel.createProperty("http://www.w3.org/2004/02/skos/core#member")
    val prefLabel = nvsModel.createProperty("http://www.w3.org/2004/02/skos/core#prefLabel")

    if (true) {
      //println(s"Loading $nvsFilename")
      nvsModel.read(new FileInputStream(nvsFilename), url, "RDF/XML")
    }
    else {
      //println(s"Loading $url")
      nvsModel.read(url, "RDF/XML")
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
