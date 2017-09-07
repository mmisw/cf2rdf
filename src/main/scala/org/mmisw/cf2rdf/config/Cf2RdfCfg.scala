package org.mmisw.cf2rdf.config

import carueda.cfg._

@Cfg
case class Cf2RdfCfg(
                      cfVersion: String,
                      xmlUrl: String,
                      destXml: String,
                      destStats: String,
                      orr: Option[OrrCfg]
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

@Cfg
case class OrrCfg(
                   endpoint:     String = "https://mmisw.org/ont/api",
                   userName:     String,
                   password:     String,
                   orgName:      String = "mmi",
                   visibility:   String = "public",
                   status:       String = "stable"
                 )
