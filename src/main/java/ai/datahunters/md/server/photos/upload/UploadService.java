package ai.datahunters.md.server.photos.upload;

import ai.datahunters.md.server.photos.upload.filesystem.FileService;
import ai.datahunters.md.server.photos.upload.uploadid.UploadId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class UploadService {
    public UploadService(FileService fileService) {
        this.fileService = fileService;
    }

    private FileService fileService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public Mono<UploadResult> handleUpload(UploadId uploadId, FilePart filePart) {
        logger.info("Starting upload for id" + uploadId);
        try {
            Path tempFile = fileService.createFileForUpload(uploadId);

            AsynchronousFileChannel channel = AsynchronousFileChannel.open(tempFile, StandardOpenOption.WRITE);

            return DataBufferUtils.write(filePart.content(), channel, 0)
                    .doOnComplete(() -> logger.info("Upload " + uploadId + " completed"))
                    .collect(Collectors.counting())
                    .map(count -> new UploadResult(uploadId, tempFile));
        } catch (IOException e) {
            return Mono.error(e);
        }
    }
}
