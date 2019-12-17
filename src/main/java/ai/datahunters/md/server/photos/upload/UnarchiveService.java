package ai.datahunters.md.server.photos.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Component
public class UnarchiveService {
    private ArchiveHandler archiveHandler;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public UnarchiveService(ArchiveHandler archiveHandler) {
        this.archiveHandler = archiveHandler;
    }

    public void unarchiveUploadedFile(FileUploaded fileUploaded) {
        logger.info("Extraction started for upload id" + fileUploaded.getUploadId());
        openFile(fileUploaded.getUploadedFilePath())
                .flatMap(this::handleUnarchive)
                .doOnError(error -> logger.error("Unarchive failed for id" + fileUploaded.getUploadId(), error))
                .subscribe(unarchived -> logger.info("Unarchived files: " + Arrays.toString(unarchived.toArray()) + "for upload id" + fileUploaded.getUploadId()));

    }

    private Mono<InputStream> openFile(Path path) {
        try {
            return Mono.just(new BufferedInputStream(new FileInputStream(path.toFile())));
        } catch (FileNotFoundException e) {
            return Mono.error(e);
        }
    }

    private Mono<List<String>> handleUnarchive(InputStream is) {
        try {
            return Mono.just(archiveHandler.probeContentAndUnarchive(is));
        } catch (IOException | ArchiveHandlerException e) {
            return Mono.error(e);
        }
    }
}
