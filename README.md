[![Known Vulnerabilities](https://snyk.io/test/github/mmisw/cf2rdf/badge.svg?targetFile=build.sbt)](https://snyk.io/test/github/mmisw/cf2rdf?targetFile=build.sbt)

## cf2rdf

The cf2rdf tool performs the following operations:

- Conversion of the [CF Standard Names](http://cfconventions.org/documents.html) vocabulary to RDF;
- Generation of mapping ontology with the corresponding [NVS ontology](http://vocab.nerc.ac.uk/collection/P07/current/);
- Registration of the generated ontologies into an ORR instance.

    With the typical configuration these generated ontologies are published as:
    - https://mmisw.org/ont/cf/parameter
    - https://mmisw.org/ont/mmi/cfonmap

## Running

Download the latest executable JAR `cf2rdf-x.y.z.jar` from https://github.com/mmisw/cf2rdf/releases/.

You will only need a [Java runtime environment](https://www.java.com/) to execute cf2rdf.

cf2rdf expects a number of parameters for its regular execution. These parameters are to be
indicated in a local `cf2rdf.conf` file on the current directory. A template of such file, with
a description of the various parameters, can be generated as follows:

    $ java -jar cf2rdf-x.y.z.jar generate-conf

Edit `cf2rdf.conf` as needed.

As command-line arguments for the regular execution, cf2rdf expects:

- either the `watchdog` argument to check for current remote CF version and trigger conversion
  and registration in case of new version (based on comparison with latest processed file):

        $ java -jar cf2rdf-x.y.z.jar watchdog

- or the desired specific steps to be performed, for example:

        $ java -jar cf2rdf-x.y.z.jar download convert register

With a typical configuration:
- The generated ontology files are:
  - `cf2rdf_output/cf-standard-name-table.rdf`
  - `cf2rdf_output/cfonmap.n3`
- which get automatically uploaded as:
  - https://mmisw.org/ont/cf/parameter
  - https://mmisw.org/ont/mmi/cfonmap
