package ai.datahunters.md.server.photos.indexing.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.UUID;

@Value
public class IndexingResponse {
    @JsonProperty("upload_id")
    UUID uploadId;
}
