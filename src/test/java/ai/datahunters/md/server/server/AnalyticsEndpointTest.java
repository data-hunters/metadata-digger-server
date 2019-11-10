package ai.datahunters.md.server.server;

import ai.datahunters.md.server.analytics.AnalyticsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.CompletableFuture;

import static ai.datahunters.md.server.server.JsonUtils.verifyJsonOutput;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AnalyticsEndpointTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AnalyticsService mockService;

    @Test
    public void endPointTest()  {

        String expectedResponse = "{\"images_number\":10}";

        BDDMockito.when(mockService.photoCount()).thenReturn(CompletableFuture.completedFuture(10L));

        webTestClient
                .get()
                .uri("/api/v1/analytics")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(respBody ->
                        verifyJsonOutput(respBody.getResponseBody(), expectedResponse)
                );
    }
}
