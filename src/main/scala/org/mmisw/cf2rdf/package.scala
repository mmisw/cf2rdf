package org.mmisw

import java.io.{File, PrintWriter}

package object cf2rdf {

  def writeFile(contents: String, filename: String): Unit =
    writeFile(contents, createOutputFile(filename) )

  def writeFile(contents: String, file: File): Unit = {
    val pw = new PrintWriter(file)
    pw.printf(contents)
    pw.close()
  }

  def createOutputFile(filename: String): File = {
    val file = new File(filename)
    file.getParentFile.mkdirs()
    file
  }
}
