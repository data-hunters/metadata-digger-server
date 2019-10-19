package ai.datahunters.md.server.solr;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrRepository;

import java.util.concurrent.CompletableFuture;

public interface PhotosRepository extends SolrRepository<Photo, String> {
    @Query("file_type:*?0*")
    public CompletableFuture<Page<Photo>> findByFileType(String searchTerm, Pageable pageable);
}
