lazy val tapirVersion = "0.13.2"
lazy val circeVersion = "0.13.0"

lazy val coreDependencies = Seq(
  "io.monix" %% "monix-bio" % "0.1.0",
  "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
  "org.scalatest" %% "scalatest" % "3.1.0" % Test,
  "org.scalatestplus" %% "scalacheck-1-14" % "3.1.0.0" % Test,
  "com.github.chocpanda" %% "scalacheck-magnolia" % "0.3.1" % Test,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "com.github.takezoe" %% "solr-scala-client" % "0.0.24",
  "com.github.pureconfig" %% "pureconfig" % "0.12.3",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-generic-extras" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion
)

ThisBuild / organization := "ai.datahunters.md"
ThisBuild / name := "metadata-digger-server"
ThisBuild / version := "0.0.1-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.1"

lazy val core = project
  .settings(
    libraryDependencies ++= coreDependencies
  )

lazy val root = (project in file("."))
  .aggregate(core)