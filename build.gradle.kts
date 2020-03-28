plugins {
    id("org.springframework.boot") version "2.2.6.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("java")
    id("io.freefair.lombok") version "5.0.0-rc6"
    id("org.sonarqube") version "2.8"
    id("com.github.ben-manes.versions") version "0.28.0"
}

group = "ai.datahunters.md"
version = "0.0.1-SNAPSHOT"
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    maven { setUrl("https://oss.jfrog.org/artifactory/oss-snapshot-local/") }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
}
sonarqube {
    properties {
        property("sonar.organization", "data-hunters")
        property("sonar.projectKey", "data-hunters_metadata-digger-server")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    implementation("org.apache.commons:commons-compress:1.20")
    implementation("org.apache.tika:tika-core:1.24")
    implementation("org.tukaani:xz:1.8")
    testImplementation("commons-codec:commons-codec:1.14")

    implementation("org.apache.solr:solr-solrj:8.5.0")
    implementation("org.springframework.boot:spring-boot-starter-data-solr:2.2.6.RELEASE")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")

    implementation("io.springfox:springfox-swagger2:3.0.0-SNAPSHOT")
    implementation("io.springfox:springfox-swagger-ui:3.0.0-SNAPSHOT")
    implementation("io.springfox:springfox-spring-webflux:3.0.0-SNAPSHOT")

    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test:3.3.4.RELEASE")

}

tasks.withType<Test> {
  useJUnitPlatform()
}
