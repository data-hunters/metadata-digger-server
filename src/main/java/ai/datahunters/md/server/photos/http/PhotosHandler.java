package ai.datahunters.md.server.photos.http;

import ai.datahunters.md.server.photos.PhotosRepository;
import ai.datahunters.md.server.photos.http.json.JsonSerializer;
import ai.datahunters.md.server.photos.http.json.read.SearchRequestRead;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class PhotosHandler {

    private PhotosRepository photosRepository;


    public Mono<ServerResponse> search(ServerRequest request) {
        return request.bodyToMono(String.class)
                .map(SearchRequestRead::read)
                .map(o -> o.orElseThrow(() -> new IllegalArgumentException("Cannot deserialize search request")))
                .flatMap(req -> Mono.fromFuture(photosRepository.search(req)))
                .map(ToApiConversions::responseFromPhotos)
                .flatMap(JsonSerializer::responseToJson)
                .flatMap(resp -> ServerResponse.ok().body(BodyInserters.fromValue(resp)));
    }
}
