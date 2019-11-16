package ai.datahunters.md.server.photos.upload;

import lombok.Value;

import java.util.List;

@Value
public class UploadResponse {
    List<String> uploaded_files;
}
