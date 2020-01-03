package ai.datahunters.md.server.photos.indexing.extract;

import ai.datahunters.md.server.photos.indexing.uploadid.IndexingJobId;
import lombok.Value;

import java.nio.file.Path;
import java.util.List;

@Value
public class FilesExtracted {
    private final IndexingJobId indexingJobId;
    private final List<Path> extractedFilesPaths;
}
