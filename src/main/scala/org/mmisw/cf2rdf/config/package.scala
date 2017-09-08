package org.mmisw.cf2rdf

import java.io.File

import com.typesafe.config.ConfigFactory

package object config {
  val configFile = new File("cf2rdf.conf")

  lazy val cfg: Cf2RdfCfg = Cf2RdfCfg(ConfigFactory.parseFile(configFile).resolve())
}
