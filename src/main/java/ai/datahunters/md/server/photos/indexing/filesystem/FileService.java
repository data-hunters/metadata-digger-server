package ai.datahunters.md.server.photos.indexing.filesystem;

import ai.datahunters.md.server.photos.indexing.uploadid.UploadId;

import java.io.IOException;
import java.nio.file.Path;

public interface FileService {
    Path createFileForUpload(UploadId uploadId) throws IOException;

    Path createDirForExtraction(UploadId uploadId) throws IOException;
}
