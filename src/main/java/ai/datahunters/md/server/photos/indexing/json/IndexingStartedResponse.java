package ai.datahunters.md.server.photos.indexing.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.UUID;

@Value
public class IndexingStartedResponse {
    @JsonProperty("indexing_job_id")
    UUID indexingJobId;
}
