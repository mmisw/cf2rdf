## cf2rdf

The cf2rdf tool performs the following operations:

- conversion of the [CF Standard Names](http://cfconventions.org/documents.html) vocabulary to RDF;
- generation of mapping with the corresponding NVS ontology;
- registration of the generated ontologies into an ORR instance. 


## Running

Download the latest executable JAR `cf2rdf-x.y.z.jar` from https://github.com/mmisw/cf2rdf/releases/.

You will only need a [Java runtime environment](https://www.java.com/) to execute cf2rdf.

cf2rdf expects a number of parameters for its regular execution. These parameters are to be
indicated in a local `cf2rdf.conf` file on the current directory. A template of such file, with
a description of the various parameters, can be generated as follows:

```shell 
$ java -jar cf2rdf-x.y.z.jar generate-conf
```

Edit `cf2rdf.conf` as needed.

As command-line arguments for the regular execution, cf2rdf expects the desired steps to be performed.
  
The complete sequence, including registration looks like so:

```shell 
$ java -jar cf2rdf-x.y.z.jar download convert register

Downloading https://raw.githubusercontent.com/cf-convention/cf-convention.github.io/master/Data/cf-standard-names/46/src/cf-standard-name-table.xml
            -> ./cf2rdf_output/cf-standard-name-table.xml

Downloading http://vocab.nerc.ac.uk/collection/P07/current/
            -> ./cf2rdf_output/nvs_P07.rdf


Replaced %20 for space in the following rdf:resource IRIs from ./cf2rdf_output/nvs_P07.rdf
	<owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_chlorine dioxide_in_air"/>
	<owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_chlorine monoxide_in_air"/>
	<owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_dichlorine peroxide_in_air"/>
	<owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_hypochlorous acid_in_air"/>
	<owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/rate_of_ hydroxyl_radical_destruction_due_to_reaction_with_nmvoc"/>

[main] WARN org.apache.jena.riot - [line: 1, col: 121] {W119} A processing instruction is in RDF content. No processing was done.

Summary: (saved in ./cf2rdf_output/cf-standard-name-table.conv-stats.txt)
	cf2rdf conversion
	input:  https://raw.githubusercontent.com/cf-convention/cf-convention.github.io/master/Data/cf-standard-names/46/src/cf-standard-name-table.xml
	output: ./cf2rdf_output/cf-standard-name-table.rdf

	vocabulary properties from input file:
	 version_number: 46; last_modified: 2017-07-25T09:41:29Z

	conversion stats:
	numConcepts = 2890
	numEntries = 2889
	numWithNoCanonicalUnits = 9
	numWithNoDefinitions = 26

	Mapping ontology:
	  mappingTermsAdded     = 2889
	  mappingOutputFilename = ./cf2rdf_output/cfonmap.n3


Registering http://mmisw.org/ont/cf/parameter - Climate and Forecast (CF) Standard Names (v.46)
    - uploading...
      POST http://localhost:8081/api/v0/ont/upload
    - registering...
      PUT http://localhost:8081/api/v0/ont {
        "orgName":"mmi",
        "name":"Climate and Forecast (CF) Standard Names (v.46)",
        "uploadedFilename":"1504822022386._guess",
        "uploadedFormat":"rdf",
        "iri":"http://mmisw.org/ont/cf/parameter",
        "status":"stable",
        "visibility":"public",
        "log":"reflect version number 46",
        "userName":"carueda"
      }
      Result:
      {
        "uri":"http://mmisw.org/ont/cf/parameter",
        "version":"20170907T150702",
        "visibility":"public",
        "status":"stable",
        "updated":"2017-09-07T15:07:02Z"
      }

Registering http://mmisw.org/ont/mmi/cfonmap - ORR-NVS CF standard name mapping (v.46)
    - uploading...
      POST http://localhost:8081/api/v0/ont/upload
    - registering...
      PUT http://localhost:8081/api/v0/ont {
        "orgName":"mmi",
        "name":"ORR-NVS CF standard name mapping (v.46)",
        "uploadedFilename":"1504822022908._guess",
        "uploadedFormat":"n3",
        "iri":"http://mmisw.org/ont/mmi/cfonmap",
        "status":"stable",
        "visibility":"public",
        "log":"reflect version number 46",
        "userName":"carueda"
      }
      Result:
      {
        "uri":"http://mmisw.org/ont/mmi/cfonmap",
        "version":"20170907T150703",
        "visibility":"public",
        "status":"stable",
        "updated":"2017-09-07T15:07:03Z"
      }
```

The latest conversion report is [here](cf2rdf_output/cf-standard-name-table.conv-stats.txt).

The generated ontologies are:
- [cf-standard-name-table.rdf](cf2rdf_output/cf-standard-name-table.rdf)
- [cfonmap.n3](cf2rdf_output/cfonmap.n3)
