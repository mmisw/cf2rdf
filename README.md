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

As command-line arguments for the regular execution, cf2rdf expects either the
`watchdog` argument to check for current remote CF version and trigger conversion
and registration in case of new version (based on comparison with latest processed file): 

```shell 
$ java -jar cf2rdf-x.y.z.jar watchdog
```

or the desired specific steps to be performed, for example:
  
```shell 
$ java -jar cf2rdf-x.y.z.jar download convert register

```

The generated ontology files are:
- `cf2rdf_output/cf-standard-name-table.rdf`
- `cf2rdf_output/cfonmap.n3`

Which get automatically uploaded (via separate mechanism) as:
- https://mmisw.org/ont/cf/parameter
- https://mmisw.org/ont/mmi/cfonmap
