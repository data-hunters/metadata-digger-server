package ai.datahunters.md.server.photos.search;

import ai.datahunters.md.server.photos.search.json.SearchRequest;
import ai.datahunters.md.server.photos.search.solr.PhotoEntity;
import org.springframework.data.solr.core.query.result.FacetPage;

import java.util.concurrent.CompletableFuture;

public interface PhotosRepository {
    CompletableFuture<Long> count();

    CompletableFuture<FacetPage<PhotoEntity>> search(SearchRequest searchTerm);
}
