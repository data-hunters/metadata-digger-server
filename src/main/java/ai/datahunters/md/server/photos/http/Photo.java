package ai.datahunters.md.server.photos.http;

import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class Photo {
    private String id;
    private String base_path;
    private String file_path;
    private String file_type;
    private List<String> directories;
    private Map<String, List<String>> meta_data;
}
