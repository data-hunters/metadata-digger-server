package ai.datahunters.md.server

import ai.datahunters.md.server.photos.PhotosEndpoint
import ai.datahunters.md.server.photos.search.{PhotosRepository, SearchError, SearchRequest, SearchResponse}
import cats.effect.ExitCode
import com.typesafe.scalalogging.StrictLogging
import monix.bio.{BIO, BIOApp, Task, UIO}

object Main extends BIOApp with StrictLogging{
  override def run(args: List[String]): UIO[ExitCode] = {
    program
      .redeem(
        err => { logger.error("Server failed", err); ExitCode.Error },
        _ => ExitCode.Success
      )
  }

  def program: Task[Unit] = {
    val config = HttpEndpoint.Configuration("localhost", 8080)
    val httpEndpoint = new HttpEndpoint(initPhotosEndpoint)
    for {
      _ <- httpEndpoint.start(config)
    } yield ()
  }

  def initPhotosEndpoint: PhotosEndpoint = {
    val repo = new PhotosRepository {
      override def search(request: SearchRequest): BIO[SearchError, SearchResponse] = ???
    }

    new PhotosEndpoint(repo)
  }
}
