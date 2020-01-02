package ai.datahunters.md.server.photos;

import ai.datahunters.md.server.photos.http.ToApiConversions;
import ai.datahunters.md.server.photos.indexing.extract.ExtractServiceService;
import ai.datahunters.md.server.photos.indexing.json.IndexingResponse;
import ai.datahunters.md.server.photos.indexing.upload.FileUploaded;
import ai.datahunters.md.server.photos.indexing.upload.UploadService;
import ai.datahunters.md.server.photos.indexing.uploadid.UploadId;
import ai.datahunters.md.server.photos.indexing.uploadid.UploadIdFactory;
import ai.datahunters.md.server.photos.search.SearchService;
import ai.datahunters.md.server.photos.search.json.SearchRequest;
import ai.datahunters.md.server.photos.search.json.SearchResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class PhotosController {

    private SearchService handler;
    private UploadService uploadService;
    private ExtractServiceService extractServiceService;
    private UploadIdFactory uploadIdFactory;

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/api/v1/photos", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SearchResponse> getPhotos(@RequestBody SearchRequest request) {
        return handler.search(request);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/api/v1/start-indexing", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<IndexingResponse> handleUpload(@RequestPart("file") Mono<FilePart> file) {
        UploadId uploadId = uploadIdFactory.build();
        return file.flatMap(filePart -> uploadService.handleUpload(uploadId, filePart))
                .onErrorMap(error -> error)
                .doOnSuccess(uploadResponse -> extractServiceService.unarchiveUploadedFile(new FileUploaded(uploadId, uploadResponse.getUploadedFilePath())))
                .map(ToApiConversions::responseFromUploadResult)
                ;
    }
}
