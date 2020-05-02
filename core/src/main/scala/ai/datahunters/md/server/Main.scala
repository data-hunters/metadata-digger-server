package ai.datahunters.md.server

import ai.datahunters.md.server.infrastructure.solr.PhotosSolrRepository
import ai.datahunters.md.server.photos.PhotosEndpoint
import cats.effect.ExitCode
import com.typesafe.scalalogging.StrictLogging
import monix.bio.{BIOApp, Task, UIO}

object Main extends BIOApp with StrictLogging{
  override def run(args: List[String]): UIO[ExitCode] = {
    program
      .redeem(
        err => { logger.error("Server failed", err); ExitCode.Error },
        _ => ExitCode.Success
      )
  }

  def program: Task[Unit] = {
    val httpConfig = HttpEndpoint.Configuration("localhost", 8080)
    val solrConfig = PhotosSolrRepository.Config("http://localhost:8983/solr/")

    val httpEndpoint = new HttpEndpoint(initPhotosEndpoint(solrConfig))
    for {
      _ <- httpEndpoint.start(httpConfig)
    } yield ()
  }

  def initPhotosEndpoint(config: PhotosSolrRepository.Config): PhotosEndpoint = {
    val repo = new PhotosSolrRepository(config)

    new PhotosEndpoint(repo)
  }
}
