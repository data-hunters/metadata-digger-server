package ai.datahunters.md.server.photos.upload.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
public class UploadResponse {
    @JsonProperty("uploaded_files")
    List<String> uploadedFiles;
}
