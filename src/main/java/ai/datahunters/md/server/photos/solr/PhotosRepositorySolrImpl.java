package ai.datahunters.md.server.photos.solr;

import ai.datahunters.md.server.photos.PhotoEntity;
import ai.datahunters.md.server.photos.PhotosRepository;
import ai.datahunters.md.server.photos.SearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

@Repository
public class PhotosRepositorySolrImpl implements PhotosRepository {

    @Resource
    private SolrTemplate solrTemplate;

    private String collectionName = "metadata_digger";

    @Override
    public CompletableFuture<Long> count() {
        return CompletableFuture.supplyAsync(() -> solrTemplate.count(collectionName, new SimpleQuery()));
    }

    @Override
    public CompletableFuture<Page<PhotoEntity>> search(SearchRequest searchTerm) {
        String queryString = searchTerm.getText_query().orElseGet(() -> "");
        SimpleQuery query = new SimpleQuery(queryString);

        return CompletableFuture.supplyAsync(() ->
                solrTemplate.queryForPage(collectionName, query, PhotoEntity.class)
        );
    }
}