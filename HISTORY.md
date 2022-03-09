# Foodmart data for hsqldb release history and change log

For a full list of releases, see
<a href="https://github.com/julianhyde/foodmart-data-hsqldb/releases">GitHub</a>.

## <a href="https://github.com/julianhyde/foodmart-data-hsqldb/releases/tag/foodmart-data-hsqldb-0.5">0.5</a> / 2022-03-08

This release changes the file format from HSQLDB 1.8 to 2.0,
and therefore supports any HSQLDB version 2.0.0 or higher;
to use 2.6.1 and higher you will need Java 11.

* Bump HSQLDB from 2.3.1 to 2.5.1, and change HSQLDB file format from 1.8 to 2.0
* Add a GitHub workflow to build and test
* Add a unit test
* Add Apache Maven wrapper
* Enable Dependabot
* [[FDH-1](https://github.com/julianhyde/foodmart-data-hsqldb/issues/1)] Schema diagram

## <a href="https://github.com/julianhyde/foodmart-data-hsqldb/releases/tag/foodmart-data-hsqldb-0.4">0.4</a> / 2015-04-07

* Set initial schema to "foodmart", so you can use unqualified table names in SQL

## <a href="https://github.com/julianhyde/foodmart-data-hsqldb/releases/tag/foodmart-data-hsqldb-0.3">0.3</a> / 2015-03-05

* Publish releases to <a href="http://search.maven.org/">Maven Central</a>
* Sign jars
* Create, based upon Pentaho mondrian-data-foodmart-hsqldb version 0.2

