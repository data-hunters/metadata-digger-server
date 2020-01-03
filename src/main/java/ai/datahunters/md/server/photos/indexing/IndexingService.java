package ai.datahunters.md.server.photos.indexing;

import ai.datahunters.md.server.photos.http.ToApiConversions;
import ai.datahunters.md.server.photos.indexing.extract.ExtractService;
import ai.datahunters.md.server.photos.indexing.json.IndexingResponse;
import ai.datahunters.md.server.photos.indexing.upload.FileUploaded;
import ai.datahunters.md.server.photos.indexing.upload.UploadService;
import ai.datahunters.md.server.photos.indexing.uploadid.IndexingJobId;
import ai.datahunters.md.server.photos.indexing.uploadid.IndexingJobIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Slf4j
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
                .doOnSuccess(fileUploaded -> extractService.extractUploadedFile(fileUploaded))
                .map(ToApiConversions::responseFromUploadResult);
    }

    public void doOnUploaded(FileUploaded fileUploaded) {
        extractService.extractUploadedFile(fileUploaded)
                .doOnError(error -> log.error("Unarchive failed for id" + fileUploaded.getIndexingJobId(), error))
                .subscribe(unarchived -> log.info("Unarchived files: " + Arrays.toString(unarchived.getExtractedFilesPaths().toArray()) + "for upload id" + fileUploaded.getIndexingJobId()));
    }
}
