package ai.datahunters.md.server.photos.indexing.extract;

import ai.datahunters.md.server.photos.indexing.filesystem.FileService;
import ai.datahunters.md.server.photos.indexing.upload.FileUploaded;
import ai.datahunters.md.server.photos.indexing.uploadid.IndexingJobId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

@Component
@Slf4j
public class ExtractService {
    private ArchiveHandler archiveHandler;
    private FileService fileService;

    public ExtractService(ArchiveHandler archiveHandler, FileService fileService) {
        this.archiveHandler = archiveHandler;
        this.fileService = fileService;
    }

    public Mono<FilesExtracted> extractUploadedFile(FileUploaded fileUploaded) {
        log.info("Extraction started for upload id" + fileUploaded.getIndexingJobId());
        return openUploadedFile(fileUploaded.getUploadedFilePath())
                .flatMap(is -> this.handleUnarchive(fileUploaded.getIndexingJobId(), is));

    }

    private Mono<InputStream> openUploadedFile(Path path) {
        try {
            return Mono.just(new BufferedInputStream(new FileInputStream(path.toFile())));
        } catch (FileNotFoundException e) {
            return Mono.error(e);
        }
    }

    private Mono<FilesExtracted> handleUnarchive(IndexingJobId indexingJobId, InputStream is) {
        return Mono.defer(() -> {
                    try {
                        Path extractionPath = fileService.createDirForExtraction(indexingJobId);
                        List<Path> extractedFilesPaths = archiveHandler.probeContentAndUnarchive(extractionPath, is);
                        return Mono.just(new FilesExtracted(indexingJobId, extractedFilesPaths));
                    } catch (IOException | ArchiveHandlerException e) {
                        throw Exceptions.propagate(e);
                    }
                }

        );
    }
}

