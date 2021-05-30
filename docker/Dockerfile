FROM openjdk:12
LABEL Description="prepare a docker image for executing codeql analysis on a repo"

RUN yum install git wget unzip maven -y

RUN echo 'create /workdir to be used'
RUN mkdir /workdir
RUN mkdir /workdir/project2scan
WORKDIR /workdir

RUN echo 'download and unzip codeql cli v.2.5.5'
RUN wget https://github.com/github/codeql-cli-binaries/releases/download/v2.5.5/codeql-linux64.zip
RUN unzip codeql-linux64.zip
RUN rm codeql-linux64.zip

RUN echo 'clone query defintions from https://github.com/github/codeql.git'
RUN git clone --depth=1 https://github.com/github/codeql.git codeql-repo

# Setup maven
RUN wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
RUN yum install -y apache-maven

ADD execute.sh ./execute.sh

ENTRYPOINT ["/workdir/execute.sh"]
