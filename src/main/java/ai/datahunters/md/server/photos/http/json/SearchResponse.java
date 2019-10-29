package ai.datahunters.md.server.photos.http.json;

import ai.datahunters.md.server.photos.http.Photo;
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

