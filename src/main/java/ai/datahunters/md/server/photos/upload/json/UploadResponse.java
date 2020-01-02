package ai.datahunters.md.server.photos.upload.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.UUID;

@Value
public class UploadResponse {
    @JsonProperty("upload_id")
    UUID uploadId;
}
