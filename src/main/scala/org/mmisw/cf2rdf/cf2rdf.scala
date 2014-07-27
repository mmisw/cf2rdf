package org.mmisw.cf2rdf

import java.io.PrintWriter
import scala.collection.mutable


/**
 * Main cf2rdf program.
 *
 * @author Carlos Rueda
 */
object cf2rdf extends App {

  /**
   * helper to process arguments and run program.
   * @param opts   map of options values
   * @param block  block to execute
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

    def getStats = {
      val propsStr = (converter.props map (kv => s"${kv._1}: ${kv._2}")) mkString "; "
      s"""cf2rdf conversion
           |date:   ${new java.util.Date()}
           |input:  $xmlFilename
           |output: $rdfFilename
           |
           |vocabulary properties from input file:
           | $propsStr
           |
           |conversion stats:
           |${converter.stats}
        """.stripMargin
    }

    def saveModel() {
      val writer = model.getWriter("RDF/XML-ABBREV")
      writer.setProperty("showXmlDeclaration", "true")
      writer.setProperty("relativeURIs", "same-document,relative")
      writer.setProperty("xmlbase", namespace)
      writer.write(model, new java.io.FileOutputStream(rdfFilename), null)
    }

    def writeStats(statsStr: String) {
      val pw = new PrintWriter(statsFilename)
      pw.printf(statsStr)
      pw.close()
    }

    saveModel()
    val statsStr = getStats
    writeStats(statsStr)
    println(statsStr)
  }
}
