package ai.datahunters.md.server.photos

import java.util.UUID

import ai.datahunters.md.server.photos.PhotosEndpoint.Json._
import ai.datahunters.md.server.photos.PhotosEndpoint.PhotosEndpointError
import ai.datahunters.md.server.photos.indexing.{
  IndexingJobId,
  IndexingService,
  StartIndexingRequest,
  StartIndexingResponse
}
import ai.datahunters.md.server.photos.search.PhotoEntity.MetaDataEntry
import ai.datahunters.md.server.photos.search._
import cats.implicits._
import com.typesafe.scalalogging.StrictLogging
import io.circe.Decoder.Result
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto._
import io.circe.{ HCursor, Codec => CirceCodec, Json => CirceJson }
import monix.bio.{ Task, UIO }
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s._

class PhotosEndpoint(photosRepository: PhotosRepository, indexingService: IndexingService) extends StrictLogging {

  val searchEndpoint: Endpoint[SearchRequest, PhotosEndpointError, SearchResponse, Nothing] =
    endpoint.post
      .in("photos")
      .in(jsonBody[SearchRequest])
      .out(jsonBody[SearchResponse])
      .errorOut(jsonBody[PhotosEndpointError])

  val startIndexingEndpoint: Endpoint[StartIndexingRequest, PhotosEndpointError, StartIndexingResponse, Nothing] =
    endpoint.post
      .in("start-indexing")
      .in(multipartBody[StartIndexingRequest])
      .out(jsonBody[StartIndexingResponse])
      .errorOut(jsonBody[PhotosEndpointError])

  def searchRoute: HttpRoutes[Task] = searchEndpoint.toRoutes { request =>
    UIO(logger.info(s"Searching for $request")) *>
    photosRepository
      .search(request)
      .mapError(e => PhotosEndpointError(e.description))
      .tapError(err => UIO(logger.error(s"Error in search: $err")))
      .flatTap(r => UIO(logger.info(s"Returning ${r.photos.length} results, total: ${r.total}")))
      .attempt
  }

  def startIndexingRoute: HttpRoutes[Task] = startIndexingEndpoint.toRoutes { request =>
    UIO(logger.info("Starting upload")) *>
    indexingService
      .handleUpload(request)
      .mapError(e => PhotosEndpointError(e.toString))
      .tapError(err => UIO(logger.error(s"Error in upload: $err")))
      .flatTap(r => UIO(logger.info(s"File uploaded, job ${r.indexingJobId} started")))
      .attempt
  }
}

object PhotosEndpoint {

  case class PhotosEndpointError(description: String)

  object Json {
    implicit val uploadCodec
        : (RawBodyType.MultipartBody, Codec[Seq[RawPart], StartIndexingRequest, CodecFormat.MultipartFormData]) =
      Codec.multipartCaseClassCodec[StartIndexingRequest]

    implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames.withDefaults

    implicit val metaDataEntryCodec: CirceCodec[MetaDataEntry] = new CirceCodec[MetaDataEntry] {
      override def apply(a: MetaDataEntry): CirceJson = a match {
        case MetaDataEntry.IntEntry(value)    => CirceJson.fromInt(value)
        case MetaDataEntry.FloatEntry(value)  => CirceJson.fromFloatOrNull(value)
        case MetaDataEntry.TextEntry(value)   => CirceJson.fromString(value)
        case MetaDataEntry.TextsEntry(values) => CirceJson.fromValues(values.map(CirceJson.fromString))
      }

      override def apply(c: HCursor): Result[MetaDataEntry] =
        c.as[Int]
          .map(MetaDataEntry.IntEntry)
          .orElse(c.as[Float].map(MetaDataEntry.FloatEntry))
          .orElse(c.as[List[String]].map(MetaDataEntry.TextsEntry))
          .orElse(c.as[String].map(MetaDataEntry.TextEntry))
    }

    implicit val photoEntityCode: CirceCodec[PhotoEntity] = deriveConfiguredCodec
    implicit val searchErrorCodec: CirceCodec[PhotosEndpointError] = deriveConfiguredCodec
    implicit val searchRequestCodec: CirceCodec[SearchRequest] = deriveConfiguredCodec
    implicit val searchResponseCodec: CirceCodec[SearchResponse] = deriveConfiguredCodec
    implicit val indexingJobIdCodec: CirceCodec[IndexingJobId] = new CirceCodec[IndexingJobId] {
      override def apply(c: HCursor): Result[IndexingJobId] = c.as[UUID].map(IndexingJobId.apply)

      override def apply(a: IndexingJobId): CirceJson = CirceJson.fromString(a.id.toString)
    }
    implicit val startIndexingResponseCodec: CirceCodec[StartIndexingResponse] = deriveConfiguredCodec
  }
}
