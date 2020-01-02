package ai.datahunters.md.server.photos.indexing;

import ai.datahunters.md.server.photos.http.ToApiConversions;
import ai.datahunters.md.server.photos.indexing.extract.ExtractService;
import ai.datahunters.md.server.photos.indexing.json.IndexingResponse;
import ai.datahunters.md.server.photos.indexing.upload.FileUploaded;
import ai.datahunters.md.server.photos.indexing.upload.UploadService;
import ai.datahunters.md.server.photos.indexing.uploadid.IndexingJobId;
import ai.datahunters.md.server.photos.indexing.uploadid.IndexingJobIdGenerator;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public class IndexingService {
    public IndexingService(UploadService uploadService, ExtractService extractService, IndexingJobIdGenerator indexingJobIdGenerator) {
        this.uploadService = uploadService;
        this.extractService = extractService;
        this.indexingJobIdGenerator = indexingJobIdGenerator;
    }

    private UploadService uploadService;
    private ExtractService extractService;
    private IndexingJobIdGenerator indexingJobIdGenerator;

    public Mono<IndexingResponse> startIndexing(Mono<FilePart> file) {
        IndexingJobId indexingJobId = indexingJobIdGenerator.build();
        return file.flatMap(filePart -> uploadService.handleUpload(indexingJobId, filePart))
                .onErrorMap(error -> error)
                .doOnSuccess(fileUploaded -> extractService.unarchiveUploadedFile(fileUploaded))
                .map(ToApiConversions::responseFromUploadResult);
    }

    public void doOnUploaded(FileUploaded fileUploaded) {

    }
}
