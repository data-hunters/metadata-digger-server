package ai.datahunters.md.server.photos.http;

import ai.datahunters.md.server.photos.http.json.JsonSerializer;
import ai.datahunters.md.server.photos.solr.PhotoEntity;
import ai.datahunters.md.server.photos.solr.PhotosRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
public class PhotosHandler {

    private PhotosRepository photosRepository;

    public PhotosHandler(PhotosRepository repo) {
        this.photosRepository = repo;
    }

    public Mono<ServerResponse> count(ServerRequest request) {
        long count = photosRepository.count();
        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromValue("Photos count" + count + ""));

    }

    public Mono<ServerResponse> getJpgs(ServerRequest request) {
        var photos = Mono.fromFuture(photosRepository.findByFileType("JPEG", PageRequest.of(0, 100))
                .thenApply(
                        p -> p.get().collect(Collectors.toList())
                )
        ).map(ToApiConversions::responseFromPhotos)
                .flatMap(JsonSerializer::responseToJson)
                .flatMap(result -> ServerResponse.ok().body(BodyInserters.fromValue(result)));

        return photos;
    }
}
