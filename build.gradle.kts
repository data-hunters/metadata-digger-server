plugins {
	id("org.springframework.boot") version "2.2.0.RELEASE"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	id("java")
    id("io.freefair.lombok") version "4.1.2"
    id("org.sonarqube") version "2.7.1"
}

group = "ai.datahunters.md"
version = "0.0.1-SNAPSHOT"
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
	mavenCentral()
    maven { setUrl("http://oss.jfrog.org/artifactory/oss-snapshot-local/") }
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
    implementation("org.apache.commons:commons-compress:1.19")
    implementation("org.apache.tika:tika-core:1.22")
    implementation("org.tukaani:xz:1.8")
    testImplementation("commons-codec:commons-codec:1.13")

    implementation("org.apache.solr:solr-solrj")
    implementation("org.springframework.boot:spring-boot-starter-data-solr")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")

    implementation("io.springfox:springfox-swagger2:3.0.0-SNAPSHOT")
    implementation("io.springfox:springfox-swagger-ui:3.0.0-SNAPSHOT")
    implementation("io.springfox:springfox-spring-webflux:3.0.0-SNAPSHOT")

	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")

}

tasks.withType<Test> {
  useJUnitPlatform()
}
