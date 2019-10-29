package ai.datahunters.md.server.server;

import ai.datahunters.md.server.photos.PhotoEntity;
import ai.datahunters.md.server.photos.PhotosRepository;
import ai.datahunters.md.server.photos.SearchRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.BDDMockito.given;
@RunWith(SpringRunner.class)
//  We create a `@SpringBootTest`, starting an actual server on a `RANDOM_PORT`
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PhotosEndpointTest {
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PhotosRepository repo;

    @Test
    public void searchByType() throws IOException, JsonProcessingException {
        var expectedRequest = SearchRequest.builder()
                .text_query(Optional.of("test"))
                .build();
        var photo = PhotoEntity.builder()
                .directories(List.of("dir"))
                .fileType("file type")
                .id("1234")
                .build();
        var page = new PageImpl<>(List.of(photo));
        given(repo.search(expectedRequest)).willReturn(CompletableFuture.completedFuture(page));

        var expectedResponseFile = Paths.get(
                getClass().getClassLoader().getResource("photosendpointtest/expected_response.json").getPath()
        );

        var expectedResponse = Files.readString(expectedResponseFile);
        webTestClient
                // Create a GET request to test an endpoint
                .post()
                .uri("/api/v1/photos")
                .body(BodyInserters.fromValue("{\"text_query\": \"test\"}"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(respBody ->
                        compareBodies(respBody.getResponseBody(), expectedResponse)
                );

    }

    void compareBodies(String receivedResponse, String expectedResonse) {
        try {
            Assert.assertEquals(mapper.readTree(receivedResponse), mapper.readTree(expectedResonse));
        } catch (JsonProcessingException e) {
            Assert.fail(e.getMessage());
        }
    }
}
