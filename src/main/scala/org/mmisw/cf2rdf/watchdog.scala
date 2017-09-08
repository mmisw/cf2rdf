package org.mmisw.cf2rdf

import org.mmisw.cf2rdf.config.{OrrCfg, cfg}

import scala.xml.Elem

object watchdog {

  def run(): Unit = {
    println(s"--cf2rdf watchdog starting --")

    val orr: OrrCfg = cfg.orr.getOrElse(
      throw new Exception("watchdog requires the 'orr' parameters"))

    val newXml = download(cfg.xmlUrl)
    val lpvXml = loadFile(cfg.destXml)

    val (newXmlIn, newXmlProps) = loadXmlString(newXml)
    val (_, lpvXmlProps) = loadXmlString(lpvXml)

    val newVersionOpt = newXmlProps.get("version_number")
    val lpvVersionOpt = lpvXmlProps.get("version_number")

    println(s"Downloaded version: ${newVersionOpt.getOrElse("?")}")
    println(s"Last processed version: ${lpvVersionOpt.getOrElse("?")}")

    if (newVersionOpt == lpvVersionOpt)
      println(s"--cf2rdf watchdog: nothing to do. --")
    else
      convertAndRegister(orr, newXml, newXmlIn, newXmlProps)
  }

  private def convertAndRegister(orr: OrrCfg,
                                 newXml: String,
                                 xmlIn: Elem,
                                 xmlProps: Map[String, String]
                                ): Unit = {

    val model = generateModel(xmlIn, xmlProps)
    saveModel(model)
    val statsStr = getSummary(xmlProps)
    writeFile(statsStr, cfg.destStats)

    val cfVersion = xmlProps.getOrElse("version_number",
      throw new Exception("Unexpected missing version_number"))

    new Registerer(orr, cfVersion).registerOntologies()

    println(s"Updating ${cfg.destXml} (with ${newXml.length} chars)")
    writeFile(newXml, cfg.destXml)

    println(s"--cf2rdf watchdog done --")
  }
}
