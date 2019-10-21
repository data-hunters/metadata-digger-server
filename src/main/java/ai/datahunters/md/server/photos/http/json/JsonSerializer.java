package ai.datahunters.md.server.photos.http.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

public class JsonSerializer {
    private static final ObjectMapper JSON = new ObjectMapper();

    public static Mono<String> responseToJson(SearchResponse resp) {
        try { return Mono.just(JsonSerializer.JSON.writeValueAsString(resp)); }
        catch (JsonProcessingException e) { return Mono.error(e); }
    }
}
