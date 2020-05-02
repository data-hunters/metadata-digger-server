package ai.datahunters.md.server.photos

import ai.datahunters.md.server.photos.PhotosEndpoint.Json._
import ai.datahunters.md.server.photos.PhotosEndpoint.PhotosEndpointError
import ai.datahunters.md.server.photos.search.PhotoEntity.MetaDataEntry
import ai.datahunters.md.server.photos.search._
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
import cats.implicits._

class PhotosEndpoint(photosRepository: PhotosRepository) extends StrictLogging {
  val searchEndpoint: Endpoint[SearchRequest, PhotosEndpointError, SearchResponse, Nothing] =
    endpoint
      .in("photos")
      .in(jsonBody[SearchRequest])
      .out(jsonBody[SearchResponse])
      .errorOut(jsonBody[PhotosEndpointError])

  def searchRoute: HttpRoutes[Task] = searchEndpoint.toRoutes { request =>
    UIO(logger.info(s"Searching for $request")) *>
    photosRepository
      .search(request)
      .mapError(e => PhotosEndpointError(e.description))
      .tapError(err => UIO(logger.error("Error in search", err)))
      .flatTap(r => UIO(logger.info(s"Returning ${r.photos.length} results, total: ${r.total}")))
      .attempt
  }
}

object PhotosEndpoint {

  case class PhotosEndpointError(description: String)

  object Json {
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
    implicit val facetFieldEncoder: CirceCodec[SearchResponse.FacetField] = deriveConfiguredCodec
    implicit val searchErrorCodec: CirceCodec[PhotosEndpointError] = deriveConfiguredCodec
    implicit val searchRequestCodec: CirceCodec[SearchRequest] = deriveConfiguredCodec
    implicit val searchResponseCodec: CirceCodec[SearchResponse] = deriveConfiguredCodec
  }
}
