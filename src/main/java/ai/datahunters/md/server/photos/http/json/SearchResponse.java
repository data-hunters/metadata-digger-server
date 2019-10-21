package ai.datahunters.md.server.photos.http.json;

import ai.datahunters.md.server.photos.http.Photo;
import lombok.Value;

import java.util.List;

@Value
public class SearchResponse {
    private List<Photo> photos;
}

