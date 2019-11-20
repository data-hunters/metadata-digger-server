package ai.datahunters.md.server.server.photos;

import ai.datahunters.md.server.photos.search.PhotosRepository;
import ai.datahunters.md.server.photos.search.json.SearchRequest;
import ai.datahunters.md.server.photos.search.solr.PhotoEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static ai.datahunters.md.server.server.testutils.JsonUtils.verifyJsonOutput;
import static org.mockito.BDDMockito.given;
@RunWith(SpringRunner.class)
//  We create a `@SpringBootTest`, starting an actual server on a `RANDOM_PORT`
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SearchPhotosEndpointTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PhotosRepository repo;

    @Test
    public void searchByType() throws IOException {
        SearchRequest expectedRequest = SearchRequest.builder()
                .textQuery(Optional.of("test"))
                .build();
        PhotoEntity photo = PhotoEntity.builder()
                .id("1234")
                .basePath("base_path")
                .filePath("file_path")
                .fileType("file type")
                .directories(Collections.singletonList("dir"))
                .metaData(prepareDynamicFields())
                .build();

        Page<PhotoEntity> page = new PageImpl<>(Collections.singletonList(photo));
        given(repo.search(expectedRequest)).willReturn(CompletableFuture.completedFuture(page));

        Path expectedResponseFile = Paths.get(
                getClass().getClassLoader().getResource("photosendpointtest/expected_response.json").getPath()
        );

        String expectedResponse = new String(Files.readAllBytes(expectedResponseFile), StandardCharsets.UTF_8);
        webTestClient
                // Create a GET request to test an endpoint
                .post()
                .uri("/api/v1/photos")
                .body(BodyInserters.fromValue("{\"text_query\": \"test\"}"))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(respBody ->
                        verifyJsonOutput(respBody.getResponseBody(), expectedResponse)
                );

    }

    Map<String, List<String>> prepareDynamicFields() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("dynamic_field_1", Arrays.asList("el11", "el12"));
        map.put("dynamic_field_2", Arrays.asList("el21", "el22"));
        return map;
    }
}
