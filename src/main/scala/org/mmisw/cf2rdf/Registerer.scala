package org.mmisw.cf2rdf

import org.mmisw.cf2rdf.config.cfg

import org.mmisw.cf2rdf.config.OrrCfg
import org.joda.time.DateTime
import org.json4s._
import org.json4s.ext.JodaTimeSerializers
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.writePretty

import scalaj.http.{Http, HttpResponse, MultiPart}


class Registerer(orr: OrrCfg, cfVersion: String) {
  private implicit val jsonFormats: Formats = DefaultFormats ++ JodaTimeSerializers.all

  def registerOntologies(): Unit = {
    registerRdf()
    registerMapping()
  }

  def registerRdf(): Unit = {
    register(cfg.rdf.iri,
      s"Climate and Forecast (CF) Standard Names (v.$cfVersion)",
      log = s"reflect version number $cfVersion",
      cfg.rdf.filename
    )
  }

  def registerMapping(): Unit = {
    register(cfg.mapping.iri,
      s"ORR-NVS CF standard name mapping (v.$cfVersion)",
      log = s"reflect version number $cfVersion",
      cfg.mapping.filename
    )
  }

  private def register(iri: String, name: String, log: String, filename: String): Unit = {
    val contents = scala.io.Source.fromFile(filename, "utf-8").mkString
    println(s"Registering $iri - $name")

    println(s"    - uploading...")
    val uploadResult = uploadOntology(iri, contents)

    println(s"    - registering...")
    val registrationResult = register(iri, name, log, uploadResult)
    println(s"      Result:\n      " +
      writePretty(registrationResult).replaceAll("\n", "\n      ") + "\n")
  }

  private def uploadOntology(iri: String, contents: String): UploadedFileInfo = {
    val route = orr.endpoint + "/v0/ont/upload"
    val method = "POST"
    println(s"      $method $route")

    val response: HttpResponse[String] = Http(route)
      .timeout(connTimeoutMs = 5*1000, readTimeoutMs = 60*1000)
      .postMulti(MultiPart("file", "filename", "text/plain", contents.getBytes))
      .auth(orr.userName, orr.password)
      .method(method)
      .asString

    if (response.code != 200) {
      throw new Exception(s"error uploading iri=$iri: response=" + response)
    }

    val json = parse(response.body)
    json.extract[UploadedFileInfo]
  }

  private def register(iri: String, name: String,
                       log: String,
                       ufi: UploadedFileInfo
                      ): OntologyRegistrationResult = {
    val params = Map(
      "iri" → iri
      , "name" → name
      , "log" → log
      , "userName" → orr.userName
      , "orgName" → orr.orgName
      , "visibility" → orr.visibility
      , "status" → orr.status
      , "uploadedFilename" → ufi.filename
      , "uploadedFormat" → ufi.format
    )

    val route = orr.endpoint + "/v0/ont"
    val method = "PUT"
    val data = writePretty(params)
    println(s"      $method $route ${data.replaceAll("\n", "\n      ")}")

    val response: HttpResponse[String] = Http(route)
      .timeout(connTimeoutMs = 5*1000, readTimeoutMs = 60*1000)
      .auth(orr.userName, orr.password)
      .postData(data)
      .header("Content-type", "application/json")
      .method(method)
      .asString

    if (200 <= response.code && response.code < 300) {
      val json = parse(response.body)
      json.extract[OntologyRegistrationResult]
    }
    else
      throw new Exception(s"error registering new version of iri=$iri: response=" + response)
  }
}

case class UploadedFileInfo(userName: String,
                            filename: String,
                            format: String
                            , possibleOntologyUris: Map[String, PossibleOntologyInfo]
                           )

case class PossibleOntologyInfo(explanations: List[String],
                                metadata: Map[String,List[String]]
                               )

case class OntologyRegistrationResult(
                                       uri:         String,
                                       version:     Option[String] = None,
                                       visibility:  Option[String] = None,
                                       status:      Option[String] = None,
                                       registered:  Option[DateTime] = None,
                                       updated:     Option[DateTime] = None,
                                       removed:     Option[DateTime] = None
                                     )
