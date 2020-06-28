# metadata-digger-server

## Local development

Perquisites: 
1. jdk 1.8+ (currently project is developed with java 8 in mind)
1. Running Solr instance with [metadata digger dev schema](https://github.com/data-hunters/metadata-digger-deployment/tree/master/dev)
1. Sbt with version at [least 1.0](https://www.scala-sbt.org/download.html) 
1. You can set up both jdk and sbt using nix shell

### Starting local server

```sbt run```

swagger ui should be accessible from http://localhost:8080/docs/index.html

### Running tests

```sbt test```
