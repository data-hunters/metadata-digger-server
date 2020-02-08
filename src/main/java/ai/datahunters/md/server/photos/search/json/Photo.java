package ai.datahunters.md.server.photos.search.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class Photo {
    private String id;
    @JsonProperty("base_path")
    private String basePath;
    @JsonProperty("file_path")
    private String filePath;
    @JsonProperty("file_type")
    private String fileType;
    @JsonProperty("directory_names")
    private List<String> directoryNames;
    @JsonProperty("tag_names")
    private List<String> tagNames;
    @JsonProperty("labels")
    private List<String> labels;
    @JsonProperty("meta_data")
    private Map<String, List<String>> metaData;
}
