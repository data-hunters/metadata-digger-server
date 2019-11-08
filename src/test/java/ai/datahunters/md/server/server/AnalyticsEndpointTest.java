package ai.datahunters.md.server.server;

import ai.datahunters.md.server.analytics.AnalyticsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AnalyticsEndpointTest {
    ObjectMapper mapper = new ObjectMapper();

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
                        compareBodies(respBody.getResponseBody(), expectedResponse)
                );
    }

    void compareBodies(String receivedResponse, String expectedResponse) {
        try {
           Assert.assertEquals(mapper.readTree(receivedResponse), mapper.readTree(expectedResponse));
        } catch (JsonProcessingException e) {
            Assert.fail(e.getMessage());
        }
    }


}
