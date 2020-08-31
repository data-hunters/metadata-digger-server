package ai.datahunters.md.server.photos

import java.io.File
import java.nio.file.Files

import ai.datahunters.md.server.infrastructure.filesystem.LocalFileSystemIndexingStorageService
import ai.datahunters.md.server.photos.PhotosEndpointSpec._
import ai.datahunters.md.server.photos.indexing.IndexingService
import ai.datahunters.md.server.photos.search._
import ai.datahunters.md.server.{ BaseTest, HttpEndpoint }
import cats.implicits._
import io.circe.parser._
import monix.bio.{ IO, Task }
import monix.execution.Scheduler.Implicits.global
import org.http4s.implicits._
import org.http4s.{ Method, Request, Response, Status }

import scala.concurrent.duration._
import scala.io.Source

class PhotosEndpointSpec extends BaseTest {
  "Photos endpoint" when {
    "reading photos" should {
      "return properly formatted json for happy path case" in {
        val request = """{ "text_query": "test", "facets": ["tag_names"] }""".stripMargin

        val photo = PhotoEntity(
          id = "1234",
          basePath = "base_path",
          filePath = "file_path",
          fileType = "file type",
          directoryNames = List("dir"),
          tagNames = List("tag"),
          labels = List("label"),
          thumbnail = "aW1hZ2VfaW5fYmFzZTY0",
          metaData = Map(
            "dynamic_field_1" -> PhotoEntity.MetaDataEntry.TextsEntry(List("el11", "el12")),
            "dynamic_field_2" -> PhotoEntity.MetaDataEntry.IntEntry(2137),
            "dynamic_field_3" -> PhotoEntity.MetaDataEntry.FloatEntry(21.37f),
            "dynamic_field_4" -> PhotoEntity.MetaDataEntry.TextEntry("text")),
          location = Some(PhotoEntity.Location(12.00, 19.17)))

        val facets: Map[Field, Map[String, Long]] = Map(Field.Labels -> Map("tag1" -> 102L, "tag2" -> 103L))
        val repositoryMock = new PhotosRepository {
          override def search(request: SearchRequest): IO[SearchError, SearchResponse] = {
            IO.now(SearchResponse(List(photo), facets, 0, 1))
          }
        }

        val storageService = buildStorageService.runSyncUnsafe(1.second)

        val indexingService = new IndexingService(storageService)

        val endpoint = new HttpEndpoint(new PhotosEndpoint(repositoryMock, indexingService))

        val requestTask = Request[Task](method = Method.POST, uri"/api/v1/photos").withEntity(request)

        checkResponse(endpoint.service.run(requestTask), Status.Ok, json)
      }
    }
  }

  def checkResponse(actual: Task[Response[Task]], expectedStatus: Status, expectedBody: String): Unit = {
    val actualResp = actual.runSyncUnsafe()

    actualResp.status shouldBe expectedStatus
    val responseBody = actualResp.as[String].runSyncUnsafe()
    parse(responseBody) shouldBe parse(expectedBody)
    ()
  }
}

object PhotosEndpointSpec {
  val testingPath = new File("target/testing/photos-endpoint")

  val buildStorageService: Task[LocalFileSystemIndexingStorageService] = for {
    _ <- IO(Files.createDirectories(testingPath.toPath))
    _ <- IO(Files.walk(testingPath.toPath).map(_.toFile.delete()))
  } yield new LocalFileSystemIndexingStorageService(testingPath)

  val json: String = Source.fromResource("search_response.json").mkString
}
