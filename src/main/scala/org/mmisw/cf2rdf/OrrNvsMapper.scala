package org.mmisw.cf2rdf

import java.io.{ByteArrayInputStream, FileInputStream}
import java.nio.charset.StandardCharsets

import org.apache.jena.ontology.OntModelSpec
import org.apache.jena.rdf.model.{ModelFactory, Resource}
import org.apache.jena.vocabulary.SKOS
import org.mmisw.orr.ont.vocabulary.{Omv, OmvMmi}

import scala.collection.JavaConverters._


class OrrNvsMapper(nvsFilename: String,
                   versionNumberOpt: Option[String],
                   lastModifiedOpt: Option[String]
                  ) {

  private val mapNamespace   = "http://mmisw.org/ont/mmi/cfonmap/"
  private val orrCfNamespace = "http://mmisw.org/ont/cf/parameter/"
  private val nvsCfNamespace = "http://vocab.nerc.ac.uk/collection/P07/current/"

  private val model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM)

  model.setNsPrefix("",    mapNamespace)
  model.setNsPrefix("orr", orrCfNamespace)
  model.setNsPrefix("nvs", nvsCfNamespace)

  model.setNsPrefix("skos", SKOS.uri)
  model.setNsPrefix("omv", Omv.NS)
  model.setNsPrefix("omvm", OmvMmi.NS)

  private val ontology = model.createOntology("http://mmisw.org/ont/mmi/cfonmap")

  ontology.addProperty(Omv.name, "ORR-NVS CF standard name mapping" +
    (if (versionNumberOpt.isDefined) s" (v.${versionNumberOpt.get})" else ""))

  ontology.addProperty(Omv.description,
    "Uses skos:exactMatch to link the IRIs of the CF standard names between the" +
      " RDF versions at the MMI ORR and NERC NVS repositories.")

  ontology.addProperty(Omv.hasCreator, "MMI")
  ontology.addProperty(OmvMmi.hasContentCreator, "MMI")

  ontology.addProperty(Omv.keywords, {
    List(
      "NetCDF", "CF", "Climate and Forecast", "self-describing", "standard names", "Canonical Units"
    ).mkString(", ")
  })

  ontology.addProperty(Omv.documentation, "https://github.com/mmisw/cf2rdf")
  ontology.addProperty(Omv.reference,     "https://github.com/mmisw/cf2rdf")

  ontology.addProperty(Omv.acronym, "cfontmap")

  lastModifiedOpt foreach { lm ⇒
    ontology.addProperty(OmvMmi.origVocLastModified, lm)
    ontology.addProperty(Omv.creationDate, lm)
  }

  ontology.addProperty(OmvMmi.hasResourceType, "http://mmisw.org/ont/mmi/resourcetype/parameter")
  ontology.addProperty(OmvMmi.origMaintainerCode, "mmi")

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
    members.asScala foreach { member =>
      numMembers += 1
      //println(s"$member")

      val resource = member.asInstanceOf[Resource]
      val prefLabels = nvsModel.listObjectsOfProperty(resource, prefLabel)
      prefLabels.asScala foreach { labelNode =>
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
