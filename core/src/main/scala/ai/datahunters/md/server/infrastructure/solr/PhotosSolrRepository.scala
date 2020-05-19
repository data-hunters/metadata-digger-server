package ai.datahunters.md.server.infrastructure.solr

import java.util
import java.util.Base64

import ai.datahunters.md.server.infrastructure.solr.PhotosSolrRepository._
import ai.datahunters.md.server.photos.search.SearchError._
import ai.datahunters.md.server.photos.search._
import cats.implicits._
import com.github.takezoe.solr.scala._
import com.typesafe.scalalogging.StrictLogging
import monix.bio.BIO

import scala.jdk.CollectionConverters._

class PhotosSolrRepository(config: Config) extends PhotosRepository {
  val client = new SolrClient(config.solrUrl)

  override def search(request: SearchRequest): BIO[SearchError, SearchResponse] = {
    val perPage = request.perPage.getOrElse(DefaultPerPage)
    val page = request.page.getOrElse(0)
    val from = perPage * page

    val baseQuery = client
      .query(request.textQuery.filterNot(_.isEmpty).getOrElse("*:*"))
      .collection(Collection)
      .rows(perPage)
      .start(from)

    val query = request.facets.fold(baseQuery)(fields => baseQuery.facetFields(fields.toSeq: _*))

    BIO(query.getResultAsMap())
      .mapError(err => SolrExecutionError(err))
      .flatMap(qr => BIO.fromEither(mapResult(page)(qr)))
  }

  private def mapResult(page: Int)(mapQueryResult: MapQueryResult): CanFail[SearchResponse] = {
    for {
      photos <- mapQueryResult.documents.traverse(mapDocument)
      facets = mapFacets(mapQueryResult.facetFields)
    } yield SearchResponse(photos = photos, facets = facets, page = page, total = mapQueryResult.numFound)
  }

  private def mapDocument(map: Map[String, Any]): CanFail[PhotoEntity] = {
    val metadata = map
      .collect {
        case (k, v) if k.startsWith("md_") =>
          k.replaceAll("md_", "") -> convertMetadaField(k, v)
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
      rawThumbnail <- getBytes("thumb_small")
      thumbnail = Base64.getEncoder.encodeToString(rawThumbnail)
      medatada <- metadata
    } yield PhotoEntity(id, basePath, filePath, fileType, directoryNames, tagNames, labels, thumbnail, medatada)
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
  private def getOptionalCollection(valuesMap: Map[String, Any])(fieldName: String): CanFail[List[String]] = {
    valuesMap.getOrElse(fieldName, new util.ArrayList[String]()) match {
      case strings: util.ArrayList[String] => Right(strings.asScala.toList)
      case field                           => Left(SolrDeserializationError(fieldName, classOf[util.ArrayList[String]], field.getClass))
    }
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
      value <- field match {
        case strings: util.ArrayList[String] => Right(strings.asScala.toList)
        case _                               => Left(SolrDeserializationError(fieldName, classOf[util.ArrayList[String]], field.getClass))
      }
    } yield value

  private def convertMetadaField(fieldName: String, field: Any): CanFail[PhotoEntity.MetaDataEntry] =
    field match {
      case al: java.util.ArrayList[String] => Right(PhotoEntity.MetaDataEntry.TextsEntry(al.asScala.toList))
      case num: Int                        => Right(PhotoEntity.MetaDataEntry.IntEntry(num))
      case num: Float                      => Right(PhotoEntity.MetaDataEntry.FloatEntry(num))
      case text: String                    => Right(PhotoEntity.MetaDataEntry.TextEntry(text))
      case rest                            => Left(SolrDeserializationError(fieldName, classOf[java.util.ArrayList[String]], rest.getClass))
    }

  private def mapFacets(facets: Map[String, Map[String, Long]]): Map[String, SearchResponse.FacetField] = {
    facets.map { case (k, v) => k -> SearchResponse.FacetField(v) }
  }

}

object PhotosSolrRepository {
  val DefaultPerPage = 20
  val Collection = "metadata_digger"
  type CanFail[A] = Either[SearchError, A]

  case class Config(solrUrl: String)

}
