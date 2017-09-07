package org.mmisw.cf2rdf

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigException.UnresolvedSubstitution

package object config {
  lazy val cfg: Cf2RdfCfg = try {
    val c = Cf2RdfCfg(ConfigFactory.load().resolve().getConfig("cf2rdf"))
    println(s"cfg = $c\n")
    c
  }
  catch {
    case e:UnresolvedSubstitution ⇒
      val m = if (e.getMessage.contains("cfVersion"))
        "\nDefine the CF_VERSION environment variable (or the cf2rdf.cfVersion system property) and try again."
      else ""

      println(
        s"""
           |ERROR: ${e.getMessage}
           |$m
         """.stripMargin)

      sys.exit(1)
  }
}
