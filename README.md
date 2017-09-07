## cf2rdf tool ##

Conversion of the [CF Standard Names](http://cfconventions.org/documents.html) 
vocabulary to RDF,
and generation of mapping with the corresponding NVS ontology. 


### Running ###

The [sbt tool](http://www.scala-sbt.org/download.html) is used to execute it.

The `cf2rdf` program reads in execution parameters from `src/main/resources/application.conf`.

With all default parameters, except for the CF version number, which 
can be indicated via the `cf2rdf.cfVersion` system property, 
the whole conversion and generation of mapping with the NVS ontology
can be run with this single command:

```shell
$ sbt -Dcf2rdf.cfVersion=46 run

Downloading https://raw.githubusercontent.com/cf-convention/cf-convention.github.io/master/Data/cf-standard-names/46/src/cf-standard-name-table.xml

Downloading http://vocab.nerc.ac.uk/collection/P07/current/

Replaced %20 for space in the following rdf:resource IRIs from src/main/resources/nvs_P07.rdf
	<owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_chlorine dioxide_in_air"/>
	<owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_chlorine monoxide_in_air"/>
	<owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_dichlorine peroxide_in_air"/>
	<owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_hypochlorous acid_in_air"/>
	<owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/rate_of_ hydroxyl_radical_destruction_due_to_reaction_with_nmvoc"/>

Summary: (saved in src/main/resources/cf-standard-name-table.conv-stats.txt)
	cf2rdf conversion
	input:  https://raw.githubusercontent.com/cf-convention/cf-convention.github.io/master/Data/cf-standard-names/46/src/cf-standard-name-table.xml
	output: src/main/resources/cf-standard-name-table.rdf

	vocabulary properties from input file:
	 version_number: 46; last_modified: 2017-07-25T09:41:29Z

	conversion stats:
	numConcepts = 2890
	numEntries = 2889
	numWithNoCanonicalUnits = 9
	numWithNoDefinitions = 26

	Mapping ontology:
	  mappingTermsAdded     = 2889
	  mappingOutputFilename = src/main/resources/cfonmap.n3
```

The latest conversion report is [here](src/main/resources/cf-standard-name-table.conv-stats.txt).

The generated ontologies are:
- [cf-standard-name-table.rdf](src/main/resources/cf-standard-name-table.rdf)
- [cfonmap.n3](src/main/resources/cfonmap.n3)


