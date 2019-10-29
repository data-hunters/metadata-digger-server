package ai.datahunters.md.server.photos;

import org.springframework.data.domain.Page;

import java.util.concurrent.CompletableFuture;

public interface PhotosRepository {
    CompletableFuture<Long> count();

    CompletableFuture<Page<PhotoEntity>> search(SearchRequest searchTerm);
}
