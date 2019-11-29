package ai.datahunters.md.server.photos.upload.filesystem;

import java.io.IOException;
import java.nio.file.Path;

public interface FileService {
    Path createFileForUpload() throws IOException;
    Path createDirForExtraction(Path path) throws IOException;
}
