FROM ubuntu:22.04
LABEL Description="prepare a docker image for executing codeql analysis on a repo"

RUN mkdir /workdir
RUN mkdir /workdir/project2scan
WORKDIR /workdir

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y ca-certificates && update-ca-certificates && \
    apt-get install -y --no-install-recommends \
    git \
    less \
    wget \
    unzip && \
    apt-get clean

#SETUP Maven
ENV MAVEN_VERSION 3.9.6
ENV MAVEN_HOME /usr/lib/mvn
ENV PATH $MAVEN_HOME/bin:$PATH

RUN wget https://downloads.apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  tar -zxvf apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  rm apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  mv apache-maven-$MAVEN_VERSION $MAVEN_HOME

#SETUP codeql cli v2.17.1
RUN wget https://github.com/github/codeql-cli-binaries/releases/download/v2.17.1/codeql-linux64.zip && \
  unzip codeql-linux64.zip && \
  rm codeql-linux64.zip

#CONFIGURE Java provided by CodeQL
ENV JAVA_HOME /workdir/codeql/tools/linux64/java
ENV PATH $JAVA_HOME/bin:$PATH
ENV CODEQL_JAVA_HOME $JAVA_HOME

#RUN echo 'clone query defintions from https://github.com/github/codeql.git'
RUN git clone --depth=1 https://github.com/github/codeql.git codeql-repo

#PRECOMPILE java-security-and-quality queries
RUN codeql/codeql query compile codeql-repo/java/ql/src/codeql-suites/java-security-and-quality.qls

ADD execute.sh /workdir/execute.sh
RUN chmod +x /workdir/execute.sh
CMD ["./execute.sh"]
