package ai.datahunters.md.server.photos.indexing;

import ai.datahunters.md.server.photos.indexing.uploadid.IndexingJobId;

public interface IndexingEvent {
    IndexingJobId getIndexingJobId();
}
