package ai.datahunters.md.server.photos.search;

import ai.datahunters.md.server.photos.search.json.SearchRequest;
import ai.datahunters.md.server.photos.search.solr.PhotoEntity;
import org.springframework.data.domain.Page;

import java.util.concurrent.CompletableFuture;

public interface PhotosRepository {
    CompletableFuture<Long> count();

    CompletableFuture<Page<PhotoEntity>> search(SearchRequest searchTerm);
}
