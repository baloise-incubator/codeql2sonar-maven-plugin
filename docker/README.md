# CodeQL analyzer for JAVA projects

This image is intended to preconfigure codeql cli and required sources to execute analysis of any JAVA project.
JAVA runtime provided by CodeQL CLI is used
> openjdk 11.0.10 2021-01-19  
> OpenJDK Runtime Environment AdoptOpenJDK (build 11.0.10+9)  
> OpenJDK 64-Bit Server VM AdoptOpenJDK (build 11.0.10+9, mixed mode)  

Pull from [DockerHub](https://hub.docker.com/r/arburk/codeql-analyzer-java): 
`docker pull arburk/codeql-analyzer-java`  
or build image like following:
`docker build . -t arburk/codeql-analyzer-java:2.14.6`

The project to scan needs to be mounted into ___/workdir/project2scan/___ like following:
`docker run -v c:/dev/repos/myJavaProject:/workdir/project2scan arburk/codeql-analyzer-java:2.14.6`
