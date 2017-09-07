## cf2rdf tool ##

Conversion of the [CF Standard Names](http://cfconventions.org/documents.html) 
vocabulary to RDF,
and generation of mapping with the corresponding NVS ontology. 

You will only need a [Java runtime environment](https://www.java.com/) 
to execute cf2rdf.


### Running ###

- Download the latest executable JAR `cf2rdf-x.y.z.jar` from https://github.com/mmisw/cf2rdf/releases/
- Required: define the `CF_VERSION` environment variable to indicate the version number to be processed
- Optional: define the `CF2RDF_OUTPUT_DIR` environment variable to indicate the output directory.
  The default output directory is `./cf2rdf_output/`.
- Run the program

For example, for version number 46, and the default output directory:

```shell
$ CF_VERSION=46 java -jar cf2rdf-x.y.z.jar

Downloading https://raw.githubusercontent.com/cf-convention/cf-convention.github.io/master/Data/cf-standard-names/46/src/cf-standard-name-table.xml

Downloading http://vocab.nerc.ac.uk/collection/P07/current/


Replaced %20 for space in the following rdf:resource IRIs from cf2rdf_output/nvs_P07.rdf
	<owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_chlorine dioxide_in_air"/>
	<owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_chlorine monoxide_in_air"/>
	<owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_dichlorine peroxide_in_air"/>
	<owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_hypochlorous acid_in_air"/>
	<owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/rate_of_ hydroxyl_radical_destruction_due_to_reaction_with_nmvoc"/>

[main] WARN org.apache.jena.riot - [line: 1, col: 121] {W119} A processing instruction is in RDF content. No processing was done.

Summary: (saved in cf2rdf_output/cf-standard-name-table.conv-stats.txt)
	cf2rdf conversion
	input:  https://raw.githubusercontent.com/cf-convention/cf-convention.github.io/master/Data/cf-standard-names/46/src/cf-standard-name-table.xml
	output: cf2rdf_output/cf-standard-name-table.rdf

	vocabulary properties from input file:
	 version_number: 46; last_modified: 2017-07-25T09:41:29Z

	conversion stats:
	numConcepts = 2890
	numEntries = 2889
	numWithNoCanonicalUnits = 9
	numWithNoDefinitions = 26

	Mapping ontology:
	  mappingTermsAdded     = 2889
	  mappingOutputFilename = cf2rdf_output/cfonmap.n3
```

The latest conversion report is [here](cf2rdf_output/cf-standard-name-table.conv-stats.txt).

The generated ontologies are:
- [cf-standard-name-table.rdf](cf2rdf_output/cf-standard-name-table.rdf)
- [cfonmap.n3](cf2rdf_output/cfonmap.n3)


The complete tool configuration is specified in [`src/main/resources/application.conf`](
https://github.com/mmisw/cf2rdf/blob/master/src/main/resources/application.conf).
Although you can override any of these parameters via various mechanisms,
typically you will only to provide the version number 
and optionally the output directory, as already indicated.
