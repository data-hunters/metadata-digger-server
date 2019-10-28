package ai.datahunters.md.server.photos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@JsonDeserialize(builder = SearchRequest.SearchRequestBuilder.class)
@Value
@Builder
public class SearchRequest {
    private Optional<String> text_query;

    @JsonPOJOBuilder(withPrefix = "")
    public static class SearchRequestBuilder {

    }
}
