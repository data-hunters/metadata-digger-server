package ai.datahunters.md.server.photos.search.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Optional;

@JsonDeserialize(builder = SearchRequest.SearchRequestBuilder.class)
@Builder
@Value
@RequiredArgsConstructor
@NonNull
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchRequest {
    @JsonProperty("text_query")
    private Optional<String> textQuery;

    @JsonPOJOBuilder(withPrefix = "")
    public static class SearchRequestBuilder {

    }
}
