package ai.datahunters.md.server.photos.http;

import lombok.Value;

import java.util.List;

@Value
public class Photo {
    private String id;
    private String file_type;
    private List<String> directories;
}
