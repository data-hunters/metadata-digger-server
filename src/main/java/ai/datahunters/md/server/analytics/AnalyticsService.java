package ai.datahunters.md.server.analytics;

import ai.datahunters.md.server.photos.search.PhotosRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Service
public class AnalyticsService  {

    private PhotosRepository repo;

    public CompletableFuture<Long> photoCount() {
            return repo.count();
   }
}
