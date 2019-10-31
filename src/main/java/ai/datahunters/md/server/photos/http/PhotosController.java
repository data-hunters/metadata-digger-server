package ai.datahunters.md.server.photos.http;

import ai.datahunters.md.server.photos.SearchRequest;
import ai.datahunters.md.server.photos.http.json.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class PhotosController {

    private PhotosHandler handler;

    @Autowired
    public PhotosController(PhotosHandler handler) {
        this.handler = handler;
    }

    @PostMapping(value = "/api/v1/photos", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SearchResponse> getPhotos(@RequestBody SearchRequest request) {
        return handler.search(request);
    }
}
