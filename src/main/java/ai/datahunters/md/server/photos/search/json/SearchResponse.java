package ai.datahunters.md.server.photos.search.json;

import ai.datahunters.md.server.photos.search.json.response.FacetFieldResult;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class SearchResponse {
    private List<Photo> photos;
    private Map<String, FacetFieldResult> facets;
    private Integer page;
    private Long total;
}

