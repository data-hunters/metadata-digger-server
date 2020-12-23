package ai.datahunters.md.server.infrastructure.solr

import java.util
import java.util.Base64

import cats.implicits._
import ai.datahunters.md.server.photos.search.{ Field, PhotoEntity, SearchError }
import ai.datahunters.md.server.photos.search.SearchError.{
  SolrDeserializationError,
  SolrMissingField,
  UnexpectedFacetField
}
import scala.jdk.CollectionConverters._

object ResponseParser {

  private[solr] def solrFacetFieldToDomain(field: String) = {
    Field.values.find(_.solrFieldName == field).toRight(UnexpectedFacetField(field))
  }

  private[solr] def mapDocument(map: Map[String, Any]): CanFail[PhotoEntity] = {
    val metadata = map
      .collect {
        case (k, v) if k.startsWith("md_") =>
          k.replaceFirst("md_", "") -> convertMetadaField(k, v)
      }
      .toList
      .traverse { case (k, v) => v.map(s => (k, s)) }
      .map(_.toMap)
    val getStr = getString(map) _
    val getList = getCollection(map) _
    val getOptList = getOptionalCollection(map) _
    val getBytes = getByteArray(map) _

    for {
      id <- getStr("id")
      basePath <- getStr("base_path")
      filePath <- getStr("file_path")
      fileType <- getStr("file_type")
      directoryNames <- getList("directory_names")
      tagNames <- getList("tag_names")
      labels <- getOptList("labels")
      rawThumbnail <- getBytes("small_thumb")
      thumbnail = Base64.getEncoder.encodeToString(rawThumbnail)
      medatada <- metadata
      location = getLocationFromMetadata(medatada)
    } yield PhotoEntity(
      id,
      basePath,
      filePath,
      fileType,
      directoryNames,
      tagNames,
      labels,
      thumbnail,
      medatada,
      location)
  }

  private def getByteArray(valuesMap: Map[String, Any])(fieldName: String): CanFail[Array[Byte]] = {
    for {
      field <- valuesMap.get(fieldName).toRight(SolrMissingField(fieldName))
      value <- field match {
        case arr: Array[Byte] => Right(arr)
        case _                => Left(SolrDeserializationError(fieldName, classOf[Array[Byte]], field.getClass))
      }
    } yield value
  }

  private def extractCollection(fieldName: String, possibleCollection: Any): CanFail[List[String]] =
    possibleCollection match {
      case str: String               => Right(List(str))
      case StringsArrayList(strings) => Right(strings)
      case field                     => Left(SolrDeserializationError(fieldName, classOf[util.ArrayList[String]], field.getClass))
    }
  private def getOptionalCollection(valuesMap: Map[String, Any])(fieldName: String): CanFail[List[String]] = {
    extractCollection(fieldName, valuesMap.getOrElse(fieldName, new util.ArrayList[String]()))
  }

  private def getLocationFromMetadata(metadata: Map[String, PhotoEntity.MetaDataEntry]) = {
    def getFloat(fieldName: String): Option[Double] = {
      metadata.get(fieldName) match {
        case Some(PhotoEntity.MetaDataEntry.FloatEntry(v)) => Some(v.toDouble)
        case _                                             => None
      }
    }

    (getFloat("gps_md_location_lat_f"), getFloat("gps_md_location_long_f")).mapN(PhotoEntity.Location)
  }

  private def getString(valuesMap: Map[String, Any])(fieldName: String): CanFail[String] =
    for {
      field <- valuesMap.get(fieldName).toRight(SolrMissingField(fieldName))
      value <- field match {
        case str: String => Right(str)
        case _           => Left(SolrDeserializationError(fieldName, classOf[String], field.getClass))
      }
    } yield value

  private def getCollection(valuesMap: Map[String, Any])(fieldName: String): CanFail[List[String]] =
    for {
      field <- valuesMap.get(fieldName).toRight(SolrMissingField(fieldName))
      value <- extractCollection(fieldName, field)
    } yield value

  private def convertMetadaField(fieldName: String, field: Any): CanFail[PhotoEntity.MetaDataEntry] =
    field match {
      case StringsArrayList(al) => Right(PhotoEntity.MetaDataEntry.TextsEntry(al))
      case num: Int             => Right(PhotoEntity.MetaDataEntry.IntEntry(num))
      case num: Float           => Right(PhotoEntity.MetaDataEntry.FloatEntry(num))
      case text: String         => Right(PhotoEntity.MetaDataEntry.TextEntry(text))
      case rest                 => Left(SolrDeserializationError(fieldName, classOf[java.util.ArrayList[String]], rest.getClass))
    }

  object StringsArrayList {
    def unapply(arg: Any): Option[List[String]] = {
      arg match {
        case array: util.ArrayList[_] =>
          val scalaArray = array.asScala
          if (scalaArray.forall(_.isInstanceOf[String])) Some(scalaArray.map(_.asInstanceOf[String]).toList)
          else None
        case _ => None
      }
    }
  }

  type CanFail[A] = Either[SearchError, A]
}
