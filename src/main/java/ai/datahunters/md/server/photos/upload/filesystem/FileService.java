package ai.datahunters.md.server.photos.upload.filesystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public interface FileService {
    Path createFileForUpload(UUID uploadId) throws IOException;

    Path createDirForExtraction() throws IOException;
}
