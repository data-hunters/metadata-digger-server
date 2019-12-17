package ai.datahunters.md.server.photos.upload;

import lombok.Value;

import java.nio.file.Path;
import java.util.UUID;

@Value
public class UploadResult {
    private final UUID uploadId;
    private final Path uploadedFilePath;
}
