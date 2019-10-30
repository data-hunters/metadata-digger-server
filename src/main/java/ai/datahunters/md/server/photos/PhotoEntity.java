package ai.datahunters.md.server.photos;

import lombok.Builder;
import lombok.Value;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Dynamic;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.List;
import java.util.Map;

@Value
@SolrDocument(collection = "metadata_digger")
@Builder
public class PhotoEntity {
    private @Id String id;
    private @Indexed(name = "base_path")
    String basePath;
    private @Indexed(name = "file_path")
    String filePath;
    private @Indexed(name = "file_type") String fileType;
    private @Indexed List<String> directories;
    private @Dynamic
    @Field("md_*")
    Map<String, List<String>> metaData;
}
