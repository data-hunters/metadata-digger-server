package ai.datahunters.md.server.photos.upload;

import java.io.IOException;
import java.nio.file.Path;

public interface FileService {
    Path createFileForUpload() throws IOException;
}
