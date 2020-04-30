package ai.datahunters.md.server.photos

import ai.datahunters.md.server.{BaseTest, HttpEndpoint}
import ai.datahunters.md.server.photos.search.{PhotoEntity, PhotosRepository, SearchError, SearchRequest, SearchResponse}
import monix.bio.{BIO, Task}
import org.http4s.{EntityDecoder, Method, Request, Response, Status, Uri}
import org.http4s.implicits._
import monix.execution.Scheduler.Implicits.global
import ai.datahunters.md.server.photos.PhotosEndpointSpec._
import io.circe.parser._

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
          metaData = Map("dynamic_field_1" -> List("el11", "el12"), "dynamic_field_2" -> List("el21", "el22")))

        val facets = Map("tag_names" -> SearchResponse.FacetField(Map("tag1" -> 102, "tag2" -> 103)))
        val repositoryMock = new PhotosRepository {
          override def search(request: SearchRequest): BIO[SearchError, SearchResponse] = {
            BIO.now(SearchResponse(List(photo), facets, 0, 1))
          }
        }

        val endpoint = new HttpEndpoint(new PhotosEndpoint(repositoryMock))

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
  val json = """{
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
               |        "dynamic_field_2": [
               |          "el21",
               |          "el22"
               |        ]
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
