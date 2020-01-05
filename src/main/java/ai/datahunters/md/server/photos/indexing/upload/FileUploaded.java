package ai.datahunters.md.server.photos.indexing.upload;

import ai.datahunters.md.server.photos.indexing.IndexingEvent;
import ai.datahunters.md.server.photos.indexing.uploadid.IndexingJobId;
import lombok.Value;

import java.nio.file.Path;

@Value
public class FileUploaded implements IndexingEvent {
    private final IndexingJobId indexingJobId;
    private final Path uploadedFilePath;
}
