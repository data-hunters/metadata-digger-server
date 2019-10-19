package ai.datahunters.md.server;

import ai.datahunters.md.server.solr.Photo;
import ai.datahunters.md.server.solr.PhotosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PhotosHandler {

    @Autowired
    PhotosRepository photosRepository;

    public Mono<ServerResponse> count(ServerRequest request) {
        long count = photosRepository.count();
        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromValue("Photos count" + count + ""));

    }

    public Mono<ServerResponse> getJpgs(ServerRequest request){
        var photos =  photosRepository.findByFileType("JPEG", PageRequest.of(0, 100));
        var result = photos.map((Photo::getId)).get().collect(Collectors.toList());
        return ServerResponse.ok().body(BodyInserters.fromValue(result.size()));
    }
}
