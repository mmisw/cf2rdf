package org.mmisw

import java.io.File

import org.apache.jena.rdf.model.Model
import org.mmisw.cf2rdf.config.cfg

import scala.xml.{Elem, Node}
import scalaj.http.{Http, HttpResponse}

package object cf2rdf {

  def download(url: String): String = {
    println(s"Downloading $url")
    val response: HttpResponse[String] = Http(url)
      .method("GET")
      .timeout(connTimeoutMs = 5*1000, readTimeoutMs = 60*1000)
      .asString

    if (response.code == 200) response.body
    else throw new Exception(
      s"""Error downloading $url
         |Code=${response.code}: ${response.statusLine}
         |${response.body}
         |""".stripMargin)
  }

  def download(url: String, filename: String): String = {
    val contents = download(url)
    writeFile(contents, filename)
    println(s"            -> $filename\n")
    contents
  }

  def loadXmlString(xml: String): (Elem, Map[String, String]) = {
    val xmlIn: Elem = scala.xml.XML.loadString(xml)
    (xmlIn, getXmlProps(xmlIn))
  }

  def loadXmlFile(filename: String): (Elem, Map[String, String]) = {
    val xmlIn: Elem = scala.xml.XML.loadFile(filename)
    (xmlIn, getXmlProps(xmlIn))
  }

  private def getXmlProps(xmlIn: Elem): Map[String, String] = {
    /** some general properties from the input */
    val keys = List("version_number", "last_modified") //, "institution", "contact")
    (keys map (k ⇒ k -> (xmlIn \ k).text.trim)).toMap
  }

  def getSummary(xmlProps: Map[String, String]): String = {
    val propsStr = (xmlProps map (kv ⇒ s"${kv._1}: ${kv._2}")) mkString "; "
    s"""cf2rdf conversion
       |input:  ${cfg.xmlUrl}
       |output: ${cfg.rdf.filename}
       |
       |vocabulary properties from input file:
       | $propsStr
       |
       |conversion stats:
       |$stats
       |""".stripMargin
  }

  def generateModel(xmlIn: Node, xmlProps: Map[String,String]): Model = {
    new Converter(xmlIn, xmlProps).convert
  }

  def saveModel(model: Model) {
    val namespace = cfg.rdf.iri + "/"
    val writer = model.getWriter(cfg.rdf.format)
    writer.setProperty("showXmlDeclaration", "true")
    writer.setProperty("relativeURIs", "same-document,relative")
    writer.setProperty("xmlbase", namespace)
    val out = new java.io.FileOutputStream(createOutputFile(cfg.rdf.filename))
    writer.write(model, out, null)
  }

  def writeFile(contents: String, filename: String): Unit =
    writeFile(contents, createOutputFile(filename) )

  def writeFile(contents: String, file: File): Unit = {
    import java.nio.charset.StandardCharsets
    import java.nio.file.Files
    val bytes = contents.getBytes(StandardCharsets.UTF_8)
    Files.write(file.toPath, bytes)
  }

  def createOutputFile(filename: String): File = {
    val file = new File(filename)
    val parent = file.getParentFile
    if(parent != null) parent.mkdirs()
    file
  }

  def loadFile(filename: String): String = {
    scala.io.Source.fromFile(filename).mkString
  }

}
