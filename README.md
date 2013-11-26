## MMI cf2rdf tool ##

Conversion of CF standard names vocabulary to RDF.


### Running the conversion ###

```shell
$ curl "http://cf-pcmdi.llnl.gov/documents/cf-standard-names/standard-name-table/current/cf-standard-name-table.xml" -o src/main/resources/cf-standard-name-table.xml
$ sbt
> run --xml src/main/resources/cf-standard-name-table.xml
[info] Running org.mmisw.cf2rdf.cf2rdf --xml src/main/resources/cf-standard-name-table.xml
generated: src/main/resources/cf-standard-name-table.rdf
           src/main/resources/cf-standard-name-table.conv-stats.txt
```
