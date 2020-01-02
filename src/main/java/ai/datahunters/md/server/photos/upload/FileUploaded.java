package ai.datahunters.md.server.photos.upload;

import ai.datahunters.md.server.photos.upload.uploadid.UploadId;
import lombok.Value;

import java.nio.file.Path;

@Value
public class FileUploaded {
    private final UploadId uploadId;
    private final Path uploadedFilePath;
}
