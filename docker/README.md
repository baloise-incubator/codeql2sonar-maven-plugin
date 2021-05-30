# CodeQL analyzer for JAVA projects

This image is intended to preconfigure codeql cli and required sources to execute analysis of any JAVA project.

Build image as following:
```docker build . -t codeql-analyzer-java:2.5.5```

The project needs to be mounted into ___/workdir/project2scan/___ like following:
```docker run codeql-analyzer-java:2.5.5 -v c:/dev/repos/myJavaProject:/workdir/project2scan```


docker run -v C:/dev/repos/arburk/codeql2sonar-maven-plugin:/workdir/project2scan codeql-analyzer-java:2.5.5 
