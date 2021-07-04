#!/bin/sh
cd /workdir/project2scan
echo "cleanup before start"
rm -rf database
mvn clean
cd ..

codeql/codeql database create project2scan/database --language=java
codeql/codeql database finalize project2scan/database
codeql/codeql database analyze project2scan/database "codeql-repo/java/ql/src/codeql-suites/java-security-and-quality.qls" --format=sarif-latest --output=/workdir/project2scan/java-analysis.sarif
