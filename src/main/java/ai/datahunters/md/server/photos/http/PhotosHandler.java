package ai.datahunters.md.server.photos.http;

import ai.datahunters.md.server.photos.PhotosRepository;
import ai.datahunters.md.server.photos.SearchRequest;
import ai.datahunters.md.server.photos.http.json.SearchResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PhotosHandler {

    private PhotosRepository photosRepository;

    public PhotosHandler(PhotosRepository repo) {
        this.photosRepository = repo;
    }


    public Mono<SearchResponse> search(SearchRequest request) {
        return Mono.fromFuture(photosRepository.search(request))
                .map(ToApiConversions::responseFromPhotos);
    }
}
