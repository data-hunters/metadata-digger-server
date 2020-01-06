package ai.datahunters.md.server.photos.indexing.uploadid;

import lombok.Value;

import java.util.UUID;

@Value
public class IndexingJobId {
    private UUID id;

    public String getName() {
        return id.toString();
    }
}
