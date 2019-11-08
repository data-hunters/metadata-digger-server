package ai.datahunters.md.server.analytics;

import ai.datahunters.md.server.photos.PhotosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AnalyticsService  {

    private PhotosRepository repo;

    @Autowired
    public AnalyticsService(PhotosRepository repo) {
        this.repo = repo;
    }

   public CompletableFuture<Long> photoCount() {
            return repo.count();
   }
}
