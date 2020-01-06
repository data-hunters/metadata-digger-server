package ai.datahunters.md.server.photos.indexing.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.UUID;

@Value
public class FileUploadedResponse implements IndexingEventResponse {
    @JsonProperty("event_type")
    String eventType = "FileUploaded";

    @JsonProperty("indexing_job_id")
    UUID indexingJobId;
}
