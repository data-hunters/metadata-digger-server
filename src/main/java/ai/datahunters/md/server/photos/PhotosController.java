package ai.datahunters.md.server.photos;

import ai.datahunters.md.server.photos.http.ToApiConversions;
import ai.datahunters.md.server.photos.indexing.IndexingService;
import ai.datahunters.md.server.photos.indexing.json.IndexingStartedResponse;
import ai.datahunters.md.server.photos.indexing.uploadid.IndexingJobId;
import ai.datahunters.md.server.photos.search.SearchService;
import ai.datahunters.md.server.photos.search.json.SearchRequest;
import ai.datahunters.md.server.photos.search.json.SearchResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@AllArgsConstructor
@RestController
public class PhotosController {

    private SearchService handler;
    private IndexingService indexingService;

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/api/v1/photos", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SearchResponse> getPhotos(@RequestBody SearchRequest request) {
        return handler.search(request);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/api/v1/start-indexing", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<IndexingStartedResponse> handleUpload(@RequestPart("file") Mono<FilePart> file) {
        return indexingService.startIndexing(file);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/api/v1/indexing-state/{indexing_id}")
    public Flux<ServerSentEvent<String>> getIndexingState(@PathVariable(name = "indexing_id") UUID indexingId) {
        return indexingService.getIndexingEvents(new IndexingJobId(indexingId))
                .map(ToApiConversions::indexingEventResponse);
    }
}
