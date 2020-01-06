package ai.datahunters.md.server.photos.indexing;

import ai.datahunters.md.server.photos.http.ToApiConversions;
import ai.datahunters.md.server.photos.indexing.extract.ExtractService;
import ai.datahunters.md.server.photos.indexing.json.IndexingStartedResponse;
import ai.datahunters.md.server.photos.indexing.upload.FileUploaded;
import ai.datahunters.md.server.photos.indexing.upload.UploadService;
import ai.datahunters.md.server.photos.indexing.uploadid.IndexingJobId;
import ai.datahunters.md.server.photos.indexing.uploadid.IndexingJobIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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
    private ConcurrentHashMap<IndexingJobId, ReplayProcessor<IndexingEvent>> eventStreams = new ConcurrentHashMap<>();

    public Mono<IndexingStartedResponse> startIndexing(Mono<FilePart> file) {
        IndexingJobId indexingJobId = indexingJobIdGenerator.build();
        return file.flatMap(filePart -> uploadService.handleUpload(indexingJobId, filePart))
                .onErrorMap(error -> error)
                .doOnSuccess(this::doOnUploaded)
                .map(ToApiConversions::responseFromUploadResult);
    }

    public Flux<IndexingEvent> getIndexingEvents(IndexingJobId jobId) {
        return getProcessor(jobId);
    }

    private IndexingEvent updateIndexingEvents(IndexingEvent event) {
        log.info("Updating indexing state with event" + event);
        getProcessor(event.getIndexingJobId()).sink().next(event);
        return event; // to convince fromCallable to work
    }

    private ReplayProcessor<IndexingEvent> getProcessor(IndexingJobId jobId) {
        ReplayProcessor<IndexingEvent> processor = Optional.ofNullable(eventStreams.get(jobId)).orElseGet(ReplayProcessor::create);
        eventStreams.putIfAbsent(jobId, processor);
        return processor;
    }

    private void doOnUploaded(FileUploaded fileUploaded) {
        Mono.delay(Duration.ofSeconds(10)).flatMap(i -> Mono.fromCallable(() -> updateIndexingEvents(fileUploaded)))

                .flatMap(ignore -> extractService.extractUploadedFile(fileUploaded))
                .map(this::updateIndexingEvents)
                .subscribe(last -> log.info("Indexing finished for job id" + last.getIndexingJobId()));
    }
}
