[![CI](https://github.com/baloise-incubator/codeql2sonar-maven-plugin/actions/workflows/ci.yml/badge.svg)](https://github.com/baloise-incubator/codeql2sonar-maven-plugin/actions/workflows/ci.yml)

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

Run to execute ```mvnw codeql2sonar:SonarIssueReporter```

## How to configure
### Mandatory properties to define
- __codeql2sonar.sarif.inputfile__: specifies the SARIF file created by CodeQL scan

### Optional properties to define
- __codeql2sonar.sarif.outputfile__: location where to write the parsed result
  Default: target/sonar/codeql2sonar.json
