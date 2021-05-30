#!/bin/bash
cd /workdir/project2scan
rm -rf database
mvn clean

../codeql/codeql database create database --language=java
../codeql/codeql database finalize database
../codeql/codeql database analyze database "../codeql-repo/java/ql/src/codeql-suites/java-security-and-quality.qls" --format=sarif-latest --output=java-analysis.sarif
