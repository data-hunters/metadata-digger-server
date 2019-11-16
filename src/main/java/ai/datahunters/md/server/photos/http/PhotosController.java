package ai.datahunters.md.server.photos.http;

import ai.datahunters.md.server.photos.SearchRequest;
import ai.datahunters.md.server.photos.http.json.SearchResponse;
import ai.datahunters.md.server.photos.upload.UploadResponse;
import ai.datahunters.md.server.photos.upload.UploadService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class PhotosController {

    private PhotosSearchService handler;
    private UploadService uploadService;

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/api/v1/photos", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SearchResponse> getPhotos(@RequestBody SearchRequest request) {
        return handler.search(request);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/api/v1/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<UploadResponse> handleUpload(@RequestPart("file") Mono<FilePart> file) {
        return file.flatMap(uploadService::handleUpload)
                .onErrorMap(error ->
                        error
                );
    }
}
