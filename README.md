#codeql2sonar-maven-plugin
It is a Maven Plugin parsing SARIF files which were created by conducted CodeQL scan. The parsed result is provided to
Sonarqube via SonarIssueReporter thus issue are displayed on Sonar's project dashboard.

##Prerequisit
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
TBD
