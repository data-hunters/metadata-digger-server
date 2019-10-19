package ai.datahunters.md.server.solr;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrRepository;

public interface PhotosRepository extends SolrRepository<Photo, String> {
    @Query("file_type:*?0*")
    public Page<Photo> findByFileType(String searchTerm, Pageable pageable);
}
