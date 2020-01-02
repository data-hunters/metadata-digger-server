package ai.datahunters.md.server.photos.indexing.extract;

import ai.datahunters.md.server.photos.indexing.filesystem.FileService;
import ai.datahunters.md.server.photos.indexing.upload.FileUploaded;
import ai.datahunters.md.server.photos.indexing.uploadid.UploadId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class ExtractServiceService {
    private ArchiveHandler archiveHandler;
    private FileService fileService;

    public ExtractServiceService(ArchiveHandler archiveHandler, FileService fileService) {
        this.archiveHandler = archiveHandler;
        this.fileService = fileService;
    }

    public void unarchiveUploadedFile(FileUploaded fileUploaded) {
        log.info("Extraction started for upload id" + fileUploaded.getUploadId());
        openFile(fileUploaded.getUploadedFilePath())
                .flatMap(is -> this.handleUnarchive(fileUploaded.getUploadId(), is))
                .doOnError(error -> log.error("Unarchive failed for id" + fileUploaded.getUploadId(), error))
                .subscribe(unarchived -> log.info("Unarchived files: " + Arrays.toString(unarchived.toArray()) + "for upload id" + fileUploaded.getUploadId()));

    }

    private Mono<InputStream> openFile(Path path) {
        try {
            return Mono.just(new BufferedInputStream(new FileInputStream(path.toFile())));
        } catch (FileNotFoundException e) {
            return Mono.error(e);
        }
    }

    private Mono<List<String>> handleUnarchive(UploadId uploadId, InputStream is) {
        try {
            Path extractionPath = fileService.createDirForExtraction(uploadId);
            return Mono.just(archiveHandler.probeContentAndUnarchive(extractionPath, is));
        } catch (IOException | ArchiveHandlerException e) {
            return Mono.error(e);
        }
    }
}
