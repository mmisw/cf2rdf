package org.mmisw.cf2rdf
import java.io.File

import org.apache.jena.sys.JenaSystem
import org.mmisw.cf2rdf.config.cfg

/**
 * Main cf2rdf program.
 */
object cf2rdf {
  JenaSystem.init()

  def main(args: Array[String]): Unit = {
    if (args.contains("generate-conf")) {
      generateConf(args)
    }
    else if (args.contains("watchdog")) {
      watchdog.run()
    }
    else {
      val givenStepNames = collection.mutable.SortedSet[String]()
      args foreach { stepName ⇒
        if (!steps.contains(stepName)) {
          System.err.println(s"invalid step $stepName.  Valid steps: ${stepNameOrder.mkString(", ")}")
          sys.exit(1)
        }
        givenStepNames += stepName
      }

      if (givenStepNames.nonEmpty) {
        stepNameOrder.filter(givenStepNames.contains) foreach { steps(_)() }
      }
      else {
        System.err.println(s"""
               |Usage:
               |   cf2rdf generate-conf [--overwrite]
               |   cf2rdf watchdog
               |   cf2rdf [download] [convert] [register]
            """.stripMargin)
      }
    }
  }

  private def generateConf(args: Array[String]): Unit = {
    val filename = "cf2rdf.conf"
    val file = new File(filename)
    if (file.exists() && !args.contains("--overwrite")) {
      System.err.println(s"$file exists.  Use --overwrite to overwrite")
      sys.exit(1)
    }
    val conf = scala.io.Source.fromInputStream(
      getClass.getClassLoader.getResource("params_template.conf").openStream()
    ).mkString
    writeFile(conf, file)
    println(s" Configuration generated: $filename\n")
  }

  private val stepNameOrder = List("download", "convert", "register")
  private val steps = Map[String, () ⇒ Any](
    "download" → downloadFiles _,
    "convert"  → generateAndSaveRdf _,
    "register" → registerOntologies _
  )

  var cfVersionOpt: Option[String] = None

  private def downloadFiles(): Unit = {
    download(cfg.xmlUrl, cfg.destXml)
    download(cfg.nvs.rdfUrl, cfg.nvs.rdfFilename)
  }

  private def generateAndSaveRdf(): Unit = {
    val (xmlIn, xmlProps) = loadXmlFile(cfg.destXml)
    val model = generateModel(xmlIn, xmlProps)
    saveModel(model)
    val statsStr = getSummary(xmlProps)
    writeFile(statsStr, cfg.destStats)
    println(s"\nSummary: (saved in ${cfg.destStats})\n\t" + statsStr.replaceAll("\n", "\n\t"))
  }

  private def registerOntologies(): Unit = {
    val cfVersion = cfVersionOpt.getOrElse(throw new Exception)
    cfg.orr foreach { new Registerer(_, cfVersion).registerOntologies() }
  }
}
