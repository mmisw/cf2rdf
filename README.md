## MMI cf2rdf tool ##

Conversion of [CF Standard Names](http://cfconventions.org/documents.html) vocabulary to RDF.


### Running the conversion ###

You will need the [sbt tool](http://www.scala-sbt.org/download.html).

Starting from the [documents page](http://cfconventions.org/documents.html), follow the links
until finding the one for the raw version of the XML file, and download that file
to `src/main/resources/`. For example:
```shell
$ curl "https://raw.githubusercontent.com/cf-convention/cf-documents/master/cf-standard-names/cf-standard-name-table-27.xml" -o src/main/resources/cf-standard-name-table.xml
```

For the ORR-NVS mapping ontology, also download the NVS RDF:
```shell
$ curl "http://vocab.nerc.ac.uk/collection/P07/current/" -o src/main/resources/nvs_P07.rdf
```

Then run the `cf2rdf` program to generate the RDF version of the CF vocabulary and also the
mapping ontology:

```shell
$ sbt run
[info] Running org.mmisw.cf2rdf.cf2rdf
cf2rdf conversion
input:  src/main/resources/cf-standard-name-table.xml
output: src/main/resources/cf-standard-name-table.rdf

vocabulary properties from input file:
 version_number: 27; last_modified: 2013-11-28T05:25:32Z

conversion stats:
numConcepts = 2526
numEntries = 2525
numWithNoCanonicalUnits = 5
numWithNoDefinitions = 31

Mapping ontology:
  mappingTermsAdded     = 2525
  mappingOutputFilename = src/main/resources/cfonmap.n3
```

This program reads in configuration parameters from `src/main/resources/application.conf`.