package ai.datahunters.md.server.photos.http.json.read;

import ai.datahunters.md.server.photos.SearchRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.io.IOException;
import java.util.Optional;

public class SearchRequestRead {
    private SearchRequestRead() {
    }
    private static final ObjectMapper JSON = buildObjectMapper();

    public static Optional<SearchRequest> read(String json) {
        try {
            return Optional.of(
                    JSON.readValue(json, SearchRequest.class)
            );
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static ObjectMapper buildObjectMapper() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        return mapper;
    }
}
