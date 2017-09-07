package org.mmisw.cf2rdf

import com.typesafe.config.ConfigFactory

package object config {
  val cfg: Cf2RdfCfg = Cf2RdfCfg(ConfigFactory.load().resolve().getConfig("cf2rdf"))
  println(s"cfg = $cfg\n")
}
