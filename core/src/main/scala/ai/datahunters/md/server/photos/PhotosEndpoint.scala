package ai.datahunters.md.server.photos
import ai.datahunters.md.server.photos.PhotosEndpoint.Json._
import ai.datahunters.md.server.photos.search._
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto._
import io.circe.{Codec => CirceCodec}
import monix.bio.Task
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s._

class PhotosEndpoint(photosRepository: PhotosRepository) {
  val searchEndpoint: Endpoint[SearchRequest, SearchError, SearchResponse, Nothing] =
    endpoint.in("photos").in(jsonBody[SearchRequest]).out(jsonBody[SearchResponse]).errorOut(jsonBody[SearchError])

  def searchRoute: HttpRoutes[Task] = searchEndpoint.toRoutes { request =>
    photosRepository.search(request).attempt
  }
}

object PhotosEndpoint {
  object Json {
    implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames.withDefaults

    implicit val photoEntityCode: CirceCodec[PhotoEntity] = deriveConfiguredCodec
    implicit val facetFieldEncoder: CirceCodec[SearchResponse.FacetField] = deriveConfiguredCodec
    implicit val searchErrorCodec: CirceCodec[SearchError] = deriveConfiguredCodec
    implicit val searchRequestCodec: CirceCodec[SearchRequest] = deriveConfiguredCodec
    implicit val searchResponseCodec: CirceCodec[SearchResponse] = deriveConfiguredCodec
  }
}
