package ai.datahunters.md.server.photos.search;

import ai.datahunters.md.server.photos.http.ToApiConversions;
import ai.datahunters.md.server.photos.search.json.SearchRequest;
import ai.datahunters.md.server.photos.search.json.SearchResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class SearchService {

    private PhotosRepository photosRepository;


    public Mono<SearchResponse> search(SearchRequest request) {
        return Mono.fromFuture(photosRepository.search(request))
                .map(ToApiConversions::responseFromPhotos);
    }
}
