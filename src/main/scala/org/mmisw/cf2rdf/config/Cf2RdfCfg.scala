package org.mmisw.cf2rdf.config

import carueda.cfg._

@Cfg
case class Cf2RdfCfg(
                      cfVersion: String,
                      xmlUrl: String,
                      destXml: String,
                      destStats: String
                    ) {
  object rdf {
    val iri:      String = $
    val format:   String = $
    val filename: String = $
  }

  object nvs {
    val rdfUrl:      String = $
    val rdfFilename: String = $
  }

  object mapping {
    val iri:      String = $
    val format:   String = $
    val filename: String = $
  }
}
