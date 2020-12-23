package ai.datahunters.md.server.photos

import ai.datahunters.md.server.photos.http.PhotosEndpointError
import ai.datahunters.md.server.photos.search.{ SearchRequest, _ }
import cats.effect.{ ContextShift, Timer }
import cats.implicits._
import com.typesafe.scalalogging.StrictLogging
import monix.bio.{ Task, UIO }
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.Http4sServerInterpreter
import ai.datahunters.md.server.photos.http.JsonCodecs._

class PhotosEndpoint(photosRepository: PhotosRepository) extends StrictLogging {

  val serarchRequestExample = SearchRequest(
    textQuery = Some("building"),
    facets = Some(Set(Field.TagNames, Field.DirectoryNames, Field.Labels, Field.FileType, Field.CameraModel)),
    page = Some(0),
    perPage = Some(10),
    filters = Some(Set(FilterToBeApplied.MultipleSelectFilter(Field.FileType, Set("JPEG")))))

  val dupa = jsonBody[Dupa]
  val searchEndpoint: Endpoint[SearchRequest, PhotosEndpointError, SearchResponse, Any] =
    endpoint.post
      .in("photos")
      .in(jsonBody[SearchRequest].example(serarchRequestExample))
      .out(jsonBody[SearchResponse])
      .errorOut(jsonBody[PhotosEndpointError])

  def performSearch(request: SearchRequest): Task[Either[PhotosEndpointError, SearchResponse]] = {
    UIO(logger.info(s"Searching for $request")) *>
    photosRepository
      .search(request)
      .mapError(e => PhotosEndpointError(e.description))
      .tapError(err => UIO(logger.error(s"Error in search: $err")))
      .flatTap(r => UIO(logger.info(s"Returning ${r.photos.length} results, total: ${r.total}")))
      .attempt
  }
  def searchRoute(implicit fcs: ContextShift[Task], timer: Timer[Task]): HttpRoutes[Task] =
    Http4sServerInterpreter.toRoutes[SearchRequest, PhotosEndpointError, SearchResponse, Task](searchEndpoint)(
      performSearch)
}
