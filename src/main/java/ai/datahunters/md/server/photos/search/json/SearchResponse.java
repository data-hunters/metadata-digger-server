package ai.datahunters.md.server.photos.search.json;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class SearchResponse {
    private List<Photo> photos;
    private Integer page;
    private Long total;
}

