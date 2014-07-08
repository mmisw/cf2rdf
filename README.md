## MMI cf2rdf tool ##

Conversion of [CF Standard Names](http://cfconventions.org/documents.html) vocabulary to RDF.


### Running the conversion ###

You will need the [sbt tool](http://www.scala-sbt.org/download.html).

Starting from the [documents page](http://cfconventions.org/documents.html), follow the links
until finding the one for the raw version of the XML file, and download that file somewhere
on your computer. Then run the `cf2rdf` program to generate the RDF version.

A complete session on the command line (using the popular [curl](http://curl.haxx.se/) for the download):

```shell
$ curl "https://raw.githubusercontent.com/cf-convention/cf-documents/master/cf-standard-names/cf-standard-name-table-27.xml" -o src/main/resources/cf-standard-name-table.xml
$ sbt
> run --xml src/main/resources/cf-standard-name-table.xml
[info] Running org.mmisw.cf2rdf.cf2rdf --xml src/main/resources/cf-standard-name-table.xml
generated: src/main/resources/cf-standard-name-table.rdf
           src/main/resources/cf-standard-name-table.conv-stats.txt
```
