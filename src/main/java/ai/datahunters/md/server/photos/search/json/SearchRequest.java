package ai.datahunters.md.server.photos.search.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@JsonDeserialize(builder = SearchRequest.SearchRequestBuilder.class)
@Value
@Builder
public class SearchRequest {
    @JsonProperty("text_query")
    private Optional<String> textQuery;

    @JsonPOJOBuilder(withPrefix = "")
    public static class SearchRequestBuilder {

    }
}
