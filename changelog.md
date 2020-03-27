2020-03-27 0.6.1

- upgrade jena from 3.3.0 to 3.14.0, which in turn brings in upgraded com.fasterxml.jackson.core:jackson-databind
  to address vulnerability reported by snyk.

  Good testing with `$ sbt "run download convert"`

- fix `assembly`.

        $ java -jar target/scala-2.12/cf2rdf-0.6.1.jar download convert
        Downloading https://raw.githubusercontent.com/cf-convention/cf-convention.github.io/master/Data/cf-standard-names/current/src/cf-standard-name-table.xml
                    -> ./cf2rdf_output/cf-standard-name-table.xml

        Downloading http://vocab.nerc.ac.uk/collection/P07/current/
                    -> ./cf2rdf_output/nvs_P07.rdf

        [main] WARN org.apache.jena.riot - [line: 1, col: 121] {W119} A processing instruction is in RDF content. No processing was done.

        Summary: (saved in ./cf2rdf_output/cf-standard-name-table.conv-stats.txt)
            cf2rdf conversion
            input:  https://raw.githubusercontent.com/cf-convention/cf-convention.github.io/master/Data/cf-standard-names/current/src/cf-standard-name-table.xml
            output: ./cf2rdf_output/cf-standard-name-table.rdf

            vocabulary properties from input file:
             version_number: 72; last_modified: 2020-03-10T11:52:02Z

            conversion stats:
            numConcepts = 4419
            numEntries = 4418
            numWithNoCanonicalUnits = 12
            numWithNoDefinitions = 19

            Mapping ontology:
              mappingTermsAdded     = 4418
              mappingOutputFilename = ./cf2rdf_output/cfonmap.n3

2017-09-08 0.6.0

- watchdog adjustments

2017-09-07 0.5.0

- add watchdog option, suitable to be used in a cronjob.
  This checks for current remote CF version and triggers conversion/registration
  in case of new version, based on comparison with latest processed file.

2017-09-07 0.4.0

- cf2rdf can now register the generated ontologies
- much simpler mechanism to specify the parameters to the program by explicitly
  using a configuration file visible to the user.

2017-09-06 0.3.0

- overall revision, improved implementation, and simpler full execution
  - upgrade various dependencies; tool version captured in build.sbt
  - more parameters captured in application configuration
  - cf2rdf now takes care of downloading the XML and the NVS RDF files
  - now assembling/releasing executable JAR
  - with all default parameters, except for the CF version number, which
    can be indicated via the `CF_VERSION` en var,
    the whole conversion and generation of mapping with NVS ontology
    can be run with this single command:
    `$ CF_VERSION=46 java -jar cf2rdf-x.y.z.jar`

2017-06-06

- capture the traditional metadata properties right in the translation so the resulting
  ontology can be registered without additional editing at the ORR.
  Exercising this with latest CF v44.
- capture the traditional metadata properties also for the mapping ontology
- TODO use ORR client library when available - for now just copying Omv and OvmMmi vocabularies

- resolve #3 "upgrade jena library"
  - now using jena 3.3.0
  - various code adjustments as Jena is now more strict.
    In concrete, for the mapping with NVS, there are invalid IRIs in
    http://vocab.nerc.ac.uk/collection/P07/current/, which Jena now complains about.
    Implemented a workaround simply consisting of replacing the offending spaces with `%20`.
    The offending IRIs are in the following lines:

            <owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_chlorine dioxide_in_air"/>
            <owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_chlorine monoxide_in_air"/>
            <owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_dichlorine peroxide_in_air"/>
            <owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_hypochlorous acid_in_air"/>
            <owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/rate_of_ hydroxyl_radical_destruction_due_to_reaction_with_nmvoc"/>

  - SKOS vocabulary provided by Jena
