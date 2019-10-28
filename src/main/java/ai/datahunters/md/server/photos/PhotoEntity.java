package ai.datahunters.md.server.photos;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.List;

@Value
@SolrDocument(collection = "metadata_digger")
@Builder
public class PhotoEntity {
    private @Id String id;
    private @Indexed(name = "file_type") String fileType;
    private @Indexed List<String> directories;
}
