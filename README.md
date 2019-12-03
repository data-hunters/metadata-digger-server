# metadata-digger-server

## Local development

Prequisites: 
1. jdk 1.8+ (currently project is developed with java 8 in mind)
1. Running Solr instance with [metadata digger dev schema](https://github.com/data-hunters/metadata-digger-deployment/tree/master/dev)

### Starting local server

```./gradlew bootRun```

swagger ui should be accessible from http://localhost:8080/swagger-ui.html

### Running tests

```./gradlew test```
