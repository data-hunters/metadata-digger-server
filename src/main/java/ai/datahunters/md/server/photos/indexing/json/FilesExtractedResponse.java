package ai.datahunters.md.server.photos.indexing.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class FilesExtractedResponse implements IndexingEventResponse {
    @JsonProperty("event_type")
    String eventType = "FilesExtracted";

    @JsonProperty("indexing_job_id")
    UUID indexingJobId;

    @JsonProperty("extracted_files")
    List<String> extractedFiles;
}
