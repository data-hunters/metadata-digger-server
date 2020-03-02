package ai.datahunters.md.server.photos.indexing.mdrunner;

import ai.datahunters.md.server.photos.indexing.IndexingEvent;
import ai.datahunters.md.server.photos.indexing.uploadid.IndexingJobId;
import lombok.Value;

@Value
public class MetadataDiggerRunFinished implements IndexingEvent {
    private final IndexingJobId indexingJobId;

}
