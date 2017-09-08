package org.mmisw.cf2rdf
import config.cfg
import java.io.{File, PrintWriter}

import org.apache.jena.rdf.model.Model
import org.apache.jena.system.JenaSystem

import scalaj.http.{Http, HttpResponse}

/**
 * Main cf2rdf program.
 */
object cf2rdf {
  JenaSystem.init()

  def main(args: Array[String]): Unit = {
    if (args.contains("generate-conf")) {
      generateConf(args)
      sys.exit(0)
    }

    val givenStepNames = collection.mutable.SortedSet[String]()
    args foreach { stepName ⇒
      if (!steps.contains(stepName)) {
        println(s"invalid step $stepName.  Valid steps: ${stepNameOrder.mkString(", ")}")
        sys.exit(1)
      }
      givenStepNames += stepName
    }

    if (givenStepNames.nonEmpty) {
      stepNameOrder.filter(givenStepNames.contains) foreach { steps(_)() }
    }
    else {
      println(s"""
             |Usage:
             |   cf2rdf generate-conf [--overwrite]
             |   cf2rdf [download] [convert] [register]
          """.stripMargin)
    }
  }

  private def generateConf(args: Array[String]): Unit = {
    val filename = "cf2rdf.conf"
    val file = new File(filename)
    if (file.exists() && !args.contains("--overwrite")) {
      println(s"$file exists.  Use --overwrite to overwrite")
      sys.exit(1)
    }
    val conf = scala.io.Source.fromInputStream(
      getClass.getClassLoader.getResource("params_template.conf").openStream()
    ).mkString
    val pw = new PrintWriter(file)
    pw.print(conf)
    pw.close()
    println(s" Configuration generated: $filename\n")
  }

  private val stepNameOrder = List("download", "convert", "register")
  private val steps = Map[String, () ⇒ Any](
    "download" → downloadFiles _,
    "convert"  → generateAndSaveRdf _,
    "register" → registerOntologies _
  )

  private def downloadFiles(): Unit = {
    download(cfg.xmlUrl, cfg.destXml)
    download(cfg.nvs.rdfUrl, cfg.nvs.rdfFilename)
  }

  private def generateAndSaveRdf(): Unit = {
    val xmlIn = scala.xml.XML.loadFile(cfg.destXml)
    val converter = new Converter(xmlIn)
    val model = converter.convert
    saveModel(model)

    val statsStr = {
      val propsStr = (converter.props map (kv ⇒ s"${kv._1}: ${kv._2}")) mkString "; "
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

    writeFile(statsStr, cfg.destStats)
    println(s"\nSummary: (saved in ${cfg.destStats})\n\t" + statsStr.replaceAll("\n", "\n\t"))
  }

  private def registerOntologies(): Unit =
    cfg.orr foreach { new Registerer(_).registerOntologies() }

  private def download(url: String, filename: String): Unit = {
    println(s"Downloading $url")
    val response: HttpResponse[String] = Http(url)
      .method("GET")
      .timeout(connTimeoutMs = 5*1000, readTimeoutMs = 60*1000)
      .asString

    val contents = if (response.code == 200) response.body
    else throw new Exception(
      s"""Error downloading $url
         |Code=${response.code}: ${response.statusLine}
         |${response.body}
         |""".stripMargin)

    val pw = new PrintWriter(createOutputFile(filename))
    pw.print(contents)
    pw.close()
    println(s"            -> $filename\n")
  }

  private def saveModel(model: Model) {
    val namespace = cfg.rdf.iri + "/"
    val writer = model.getWriter(cfg.rdf.format)
    writer.setProperty("showXmlDeclaration", "true")
    writer.setProperty("relativeURIs", "same-document,relative")
    writer.setProperty("xmlbase", namespace)
    val out = new java.io.FileOutputStream(createOutputFile(cfg.rdf.filename))
    writer.write(model, out, null)
  }
}
