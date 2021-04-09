[![CI](https://github.com/baloise-incubator/codeql2sonar-maven-plugin/actions/workflows/ci.yml/badge.svg)](https://github.com/baloise-incubator/codeql2sonar-maven-plugin/actions/workflows/ci.yml)
[![CodeQL](https://github.com/baloise-incubator/codeql2sonar-maven-plugin/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/baloise-incubator/codeql2sonar-maven-plugin/actions/workflows/codeql-analysis.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=baloise-incubator_codeql2sonar-maven-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=baloise-incubator_codeql2sonar-maven-plugin)

# codeql2sonar-maven-plugin
It is a Maven Plugin parsing SARIF files which were created by conducted CodeQL scan. The parsed result is provided to
Sonarqube via SonarIssueReporter thus issue are displayed on Sonar's project dashboard.

## Prerequisit
In order to use this plugin properly, your JAVA project needs to be configured in Sonar already and connected using 
___org.sonarsource.scanner.maven:sonar-maven-plugin___

## How to use
add the following plugin to your pom.xml
```XML
<plugin>
  <groupId>com.baloise.open</groupId>
  <artifactId>codeql2sonar-maven-plugin</artifactId>
  <version>1.0-SNAPSHOT</version>
</plugin>
```

Run to execute ```mvn codeql2sonar:SonarIssueReporter```

## How to configure
### Mandatory properties
- __codeql2sonar.sarif.inputfile__: specifies the SARIF file created by CodeQL scan

### Optional properties
- __codeql2sonar.sarif.outputfile__: location where to write the parsed result.
  <br/>_Default: target/sonar/codeql2sonar.json_
- __codeql2sonar.sarif.ignoreTests__: if set to true, resources containing '/test/' in artifact location 
  are not reported to Sonar.<br/>_Default: false_
- __codeql2sonar.sarif.path.excludes__: Array of artifact locations to be excluded from result.
  Regex-patterns can be used here according to pattern ```.*<codeql2sonar.sarif.path.excludes.value>.*``` 
  while patterns are compiled case-insensitive.
  <br/>Example:
  ```xml
  <codeql2sonar.sarif.path.excludes>
    <param>value1</param>
    <param>value2</param>
  </codeql2sonar.sarif.path.excludes>
  ```
