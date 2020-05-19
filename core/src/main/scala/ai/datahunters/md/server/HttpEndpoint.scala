package ai.datahunters.md.server

import ai.datahunters.md.server.HttpEndpoint._
import ai.datahunters.md.server.photos.PhotosEndpoint
import cats.effect.ConcurrentEffect
import cats.implicits._
import monix.bio.{Task, UIO}
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._

class HttpEndpoint( photosEndpoint: PhotosEndpoint) {
  private val routes = photosEndpoint.searchRoute <+> photosEndpoint.startIndexingRoute

  val service: HttpApp[Task] = Router(apiPath -> routes).orNotFound

  def start(config: Configuration)(implicit ce: ConcurrentEffect[Task]): Task[Unit] = {
    BlazeServerBuilder[Task]
      .bindHttp(config.port, config.host)
      .withHttpApp(service)
      .serve
      .compile
      .drain
  }

}
object HttpEndpoint {
  val apiPath = "api/v1"

  case class Configuration(host: String, port: Int)
}