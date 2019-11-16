package ai.datahunters.md.server.photos.http;

import ai.datahunters.md.server.photos.PhotosRepository;
import ai.datahunters.md.server.photos.SearchRequest;
import ai.datahunters.md.server.photos.http.json.SearchResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class PhotosSearchService {

    private PhotosRepository photosRepository;


    public Mono<SearchResponse> search(SearchRequest request) {
        return Mono.fromFuture(photosRepository.search(request))
                .map(ToApiConversions::responseFromPhotos);
    }
}
