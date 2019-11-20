package ai.datahunters.md.server.server.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;

public class JsonUtils {

    private static ObjectMapper mapper = new ObjectMapper();

    public static void verifyJsonOutput(String receivedResponse, String expectedResponse) {
        try {
            Assert.assertEquals(mapper.readTree(receivedResponse), mapper.readTree(expectedResponse));
        } catch (JsonProcessingException e) {
            Assert.fail(e.getMessage());
        }
    }
}
