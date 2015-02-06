## MMI cf2rdf tool ##

Conversion of [CF Standard Names](http://cfconventions.org/documents.html) vocabulary to RDF.


### Running the conversion ###

First you need to download the latest version of the XML file. Using your browser:

- visit the [cf-convention.github.io Github repository](https://github.com/cf-convention/cf-convention.github.io)
- follow the links until you find the latest version of the XML file in the Github space. For example,
  https://github.com/cf-convention/cf-convention.github.io/blob/master/Data/cf-standard-names/28/src/cf-standard-name-table.xml
- click the "Raw" representation to download the file
- copy that file under `src/main/resources/`.

Example using curl for the download:

```shell
$ FOUND_XML=https://raw.githubusercontent.com/cf-convention/cf-convention.github.io/master/Data/cf-standard-names/28/src/cf-standard-name-table.xml
$ curl "$FOUND_XML" -o src/main/resources/cf-standard-name-table.xml
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100 2348k  100 2348k    0     0   893k      0  0:00:02  0:00:02 --:--:--  893k
```

For the ORR-NVS mapping ontology, also download the NVS RDF:

```shell
$ curl "http://vocab.nerc.ac.uk/collection/P07/current/" -o src/main/resources/nvs_P07.rdf
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100 4771k    0 4771k    0     0   237k      0 --:--:--  0:00:20 --:--:--  885k
```

Then run the `cf2rdf` program to generate the RDF version of the CF vocabulary
and also the mapping ontology.
This program is run with the [sbt tool](http://www.scala-sbt.org/download.html):


```shell
$ sbt run
cf2rdf conversion
input:  src/main/resources/cf-standard-name-table.xml
output: src/main/resources/cf-standard-name-table.rdf

vocabulary properties from input file:
 version_number: 28; last_modified: 2015-01-07T10:24:11Z

conversion stats:
numConcepts = 2640
numEntries = 2639
numWithNoCanonicalUnits = 5
numWithNoDefinitions = 31

Mapping ontology:
  mappingTermsAdded     = 2638
  mappingOutputFilename = src/main/resources/cfonmap.n3
```

As indicated in the output, the generated ontologies are:
- `src/main/resources/cf-standard-name-table.rdf`
- `src/main/resources/cfonmap.n3`


This program reads in configuration parameters from `src/main/resources/application.conf`.