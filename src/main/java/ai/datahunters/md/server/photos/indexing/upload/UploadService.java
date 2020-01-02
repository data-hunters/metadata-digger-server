package ai.datahunters.md.server.photos.indexing.upload;

import ai.datahunters.md.server.photos.indexing.filesystem.FileService;
import ai.datahunters.md.server.photos.indexing.uploadid.IndexingJobId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UploadService {
    public UploadService(FileService fileService) {
        this.fileService = fileService;
    }

    private FileService fileService;

    public Mono<FileUploaded> handleUpload(IndexingJobId indexingJobId, FilePart filePart) {
        log.info("Starting upload for id" + indexingJobId);
        try {
            Path tempFile = fileService.createFileForUpload(indexingJobId);

            AsynchronousFileChannel channel = AsynchronousFileChannel.open(tempFile, StandardOpenOption.WRITE);

            return DataBufferUtils.write(filePart.content(), channel, 0)
                    .doOnComplete(() -> log.info("Upload " + indexingJobId + " completed"))
                    .collect(Collectors.counting())
                    .map(count -> new FileUploaded(indexingJobId, tempFile));
        } catch (IOException e) {
            return Mono.error(e);
        }
    }
}
