package ai.datahunters.md.server.photos.search.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Value
@JsonInclude(JsonInclude.Include.NON_ABSENT)
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

    @JsonProperty("hash_crc32")
    private Optional<String> hashCrc32;

    @JsonProperty("hash_md5")
    private Optional<String> hashMd5;

    @JsonProperty("hash_sha1")
    private Optional<String> hashSha1;

    @JsonProperty("hash_sha224")
    private Optional<String> hashSha224;

    @JsonProperty("hash_sha256")
    private Optional<String> hashSha256;

    @JsonProperty("hash_sha384")
    private Optional<String> hashSha284;

    @JsonProperty("hash_sha512")
    private Optional<String> hashSha512;
}
