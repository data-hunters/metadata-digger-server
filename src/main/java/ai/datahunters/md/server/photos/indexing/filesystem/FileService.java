package ai.datahunters.md.server.photos.indexing.filesystem;

import ai.datahunters.md.server.photos.indexing.uploadid.IndexingJobId;

import java.io.IOException;
import java.nio.file.Path;

public interface FileService {
    Path createFileForUpload(IndexingJobId indexingJobId) throws IOException;

    Path createDirForExtraction(IndexingJobId indexingJobId) throws IOException;
}
