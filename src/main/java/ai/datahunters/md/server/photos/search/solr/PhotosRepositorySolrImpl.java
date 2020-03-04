package ai.datahunters.md.server.photos.search.solr;

import ai.datahunters.md.server.photos.search.PhotosRepository;
import ai.datahunters.md.server.photos.search.json.SearchRequest;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.FacetPage;
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
        return CompletableFuture.supplyAsync(() -> solrTemplate.count(collectionName, new SimpleQuery("*:*")));
    }

    @Override
    public CompletableFuture<FacetPage<PhotoEntity>> search(SearchRequest searchTerm) {
        String queryString = searchTerm.getTextQuery().orElseGet(() -> "*:*");
        FacetQuery query = new SimpleFacetQuery(new SimpleStringCriteria(queryString));
        if (!searchTerm.getFacets().isEmpty()) {
            FacetOptions facetOptions = new FacetOptions();
            searchTerm.getFacets().forEach(field -> query.setFacetOptions(facetOptions.addFacetOnField(field)));
            query.setFacetOptions(facetOptions);
        }
        return CompletableFuture.supplyAsync(() ->
                solrTemplate.queryForFacetPage(collectionName, query, PhotoEntity.class)
        );
    }
}
