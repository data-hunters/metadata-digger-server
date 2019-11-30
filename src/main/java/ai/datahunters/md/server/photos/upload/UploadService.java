package ai.datahunters.md.server.photos.upload;

import ai.datahunters.md.server.photos.http.ToApiConversions;
import ai.datahunters.md.server.photos.upload.filesystem.FileService;
import ai.datahunters.md.server.photos.upload.json.UploadResponse;
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
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class UploadService {
    public UploadService(FileService fileService) {
        this.fileService = fileService;
    }

    private FileService fileService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public Mono<UploadResponse> handleUpload(FilePart filePart) {
        try {
            Path tempFile = fileService.createFileForUpload();

            AsynchronousFileChannel channel = AsynchronousFileChannel.open(tempFile, StandardOpenOption.WRITE);

            return DataBufferUtils.write(filePart.content(), channel, 0)
                    .doOnComplete(() -> logger.info("Upload completed"))
                    .collect(Collectors.counting())
                    .map(count -> Collections.singletonList(tempFile.toAbsolutePath().toString()))
                    .map(ToApiConversions::responseFromUploadedFiles);
        } catch (IOException e) {
            return Mono.error(e);
        }
    }
}
