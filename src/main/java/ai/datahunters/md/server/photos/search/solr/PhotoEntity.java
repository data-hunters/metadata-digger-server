package ai.datahunters.md.server.photos.search.solr;

import lombok.Builder;
import lombok.Value;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Dynamic;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Value
@SolrDocument(collection = "metadata_digger")
@Builder
public class PhotoEntity {
    private @Id
    String id;

    private @Indexed(name = "base_path")
    String basePath;

    private @Indexed(name = "file_path")
    String filePath;

    private @Indexed(name = "file_type")
    String fileType;

    private @Indexed(name = "directory_names")
    List<String> directoryNames;

    private @Indexed(name = "tag_names")
    List<String> tagNames;

    private @Indexed(name = "labels")
    List<String> labels;

    private @Indexed(name = "hash_crc32")
    Optional<String> hashCrc32 = Optional.empty();

    private @Indexed(name = "hash_md5")
    Optional<String> hashMd5 = Optional.empty();

    private @Indexed(name = "hash_sha1")
    Optional<String> hashSha1 = Optional.empty();

    private @Indexed(name = "hash_sha224")
    Optional<String> hashSha224 = Optional.empty();

    private @Indexed(name = "hash_sha256")
    Optional<String> hashSha256 = Optional.empty();

    private @Indexed(name = "hash_sha384")
    Optional<String> hashSha284 = Optional.empty();

    private @Indexed(name = "hash_sha512")
    Optional<String> hashSha512 = Optional.empty();

    private @Dynamic
    @Field("md_*")
    Map<String, List<String>> metaData;
}
