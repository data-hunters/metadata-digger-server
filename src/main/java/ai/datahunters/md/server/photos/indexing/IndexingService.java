package ai.datahunters.md.server.photos.indexing;

import ai.datahunters.md.server.photos.http.ToApiConversions;
import ai.datahunters.md.server.photos.indexing.extract.ExtractService;
import ai.datahunters.md.server.photos.indexing.json.IndexingResponse;
import ai.datahunters.md.server.photos.indexing.upload.FileUploaded;
import ai.datahunters.md.server.photos.indexing.upload.UploadService;
import ai.datahunters.md.server.photos.indexing.uploadid.UploadId;
import ai.datahunters.md.server.photos.indexing.uploadid.UploadIdFactory;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public class IndexingService {
    public IndexingService(UploadService uploadService, ExtractService extractService, UploadIdFactory uploadIdFactory) {
        this.uploadService = uploadService;
        this.extractService = extractService;
        this.uploadIdFactory = uploadIdFactory;
    }

    private UploadService uploadService;
    private ExtractService extractService;
    private UploadIdFactory uploadIdFactory;

    public Mono<IndexingResponse> startIndexing(Mono<FilePart> file) {
        UploadId uploadId = uploadIdFactory.build();
        return file.flatMap(filePart -> uploadService.handleUpload(uploadId, filePart))
                .onErrorMap(error -> error)
                .doOnSuccess(uploadResponse -> extractService.unarchiveUploadedFile(new FileUploaded(uploadId, uploadResponse.getUploadedFilePath())))
                .map(ToApiConversions::responseFromUploadResult);
    }
}
