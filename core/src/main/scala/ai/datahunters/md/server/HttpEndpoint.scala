package ai.datahunters.md.server

import ai.datahunters.md.server.HttpEndpoint._
import ai.datahunters.md.server.photos.PhotosEndpoint
import cats.effect.ConcurrentEffect
import cats.implicits._
import monix.bio.Task
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._
import org.http4s.server.middleware.CORS
import org.http4s.{ HttpApp, HttpRoutes }
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.Server
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s

class HttpEndpoint(photosEndpoint: PhotosEndpoint) {
  private val routes = CORS(photosEndpoint.searchRoute <+> photosEndpoint.startIndexingRoute)

  val openApiDefinition: String =
    List(photosEndpoint.searchEndpoint, photosEndpoint.startIndexingEndpoint)
      .toOpenAPI("Meta data digger", "0.1")
      .servers(List(Server(s"http://localhost:8080/$apiPath")))
      .toYaml

  val swaggerRoute: HttpRoutes[Task] = new SwaggerHttp4s(openApiDefinition).routes[Task]

  val service: HttpApp[Task] = Router(apiPath -> routes, "/" -> swaggerRoute).orNotFound

  def start(config: Configuration)(implicit ce: ConcurrentEffect[Task]): Task[Unit] = {
    BlazeServerBuilder[Task].bindHttp(config.port, config.host).withHttpApp(service).serve.compile.drain
  }

}
object HttpEndpoint {
  val apiPath = "api/v1"

  case class Configuration(host: String, port: Int)
}
