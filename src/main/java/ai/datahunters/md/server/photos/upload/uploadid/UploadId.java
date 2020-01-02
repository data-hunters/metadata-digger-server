package ai.datahunters.md.server.photos.upload.uploadid;

import lombok.Value;

import java.util.UUID;

@Value
public class UploadId {
    private UUID id;

    public String getName() {
        return id.toString();
    }
}
