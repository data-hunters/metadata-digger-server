package ai.datahunters.md.server.photos

import java.io.File
import java.nio.file.{ Files, Path }

import ai.datahunters.md.server.infrastructure.filesystem.LocalFileSystemIndexingStorageService
import ai.datahunters.md.server.photos.PhotosEndpointSpec._
import ai.datahunters.md.server.photos.indexing.IndexingService
import ai.datahunters.md.server.photos.search._
import ai.datahunters.md.server.{ BaseTest, HttpEndpoint }
import io.circe.parser._
import monix.bio.{ BIO, Task }
import monix.execution.Scheduler.Implicits.global
import org.http4s.implicits._
import org.http4s.{ Method, Request, Response, Status }
import cats.implicits._

import scala.concurrent.duration._

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
            "dynamic_field_4" -> PhotoEntity.MetaDataEntry.TextEntry("text")))

        val facets = Map("tag_names" -> SearchResponse.FacetField(Map("tag1" -> 102, "tag2" -> 103)))
        val repositoryMock = new PhotosRepository {
          override def search(request: SearchRequest): BIO[SearchError, SearchResponse] = {
            BIO.now(SearchResponse(List(photo), facets, 0, 1))
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
  }
}

object PhotosEndpointSpec {
  val testingPath = new File("target/testing/photos-endpoint")

  val buildStorageService: Task[LocalFileSystemIndexingStorageService] = for {
    _ <- BIO(Files.createDirectories(testingPath.toPath))
    _ <- BIO(Files.walk(testingPath.toPath).map(_.toFile.delete()))
  } yield new LocalFileSystemIndexingStorageService(testingPath)

  val json: String =
    """{
      |  "photos": [
      |    {
      |      "id": "1234",
      |      "base_path": "base_path",
      |      "file_path": "file_path",
      |      "file_type": "file type",
      |      "directory_names": [
      |        "dir"
      |      ],
      |      "tag_names": [
      |        "tag"
      |      ],
      |      "labels": [
      |        "label"
      |      ],
      |      "thumbnail": "aW1hZ2VfaW5fYmFzZTY0",
      |      "meta_data": {
      |        "dynamic_field_1": [
      |          "el11",
      |          "el12"
      |        ],
      |        "dynamic_field_2": 2137,
      |        "dynamic_field_3": 21.37,
      |        "dynamic_field_4": "text"
      |      }
      |    }
      |  ],
      |  "facets": {
      |    "tag_names": {
      |      "results": {
      |        "tag1": 102,
      |        "tag2": 103
      |      }
      |    }
      |  },
      |  "page": 0,
      |  "total": 1
      |}""".stripMargin
}
