package ai.datahunters.md.server.photos.upload;

import ai.datahunters.md.server.photos.http.ToApiConversions;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UploadService {
    public Mono<UploadResponse> handleUpload(FilePart filePart) {
        try {
            String fileName = UUID.randomUUID().toString();
            Path tempFile = Files.createTempFile("upload", fileName);

            AsynchronousFileChannel channel = AsynchronousFileChannel.open(tempFile, StandardOpenOption.WRITE);

            return DataBufferUtils.write(filePart.content(), channel, 0)
                    .doOnComplete(() -> System.out.println("uploaded"))
                    .collect(Collectors.counting())
                    .map(count -> List.of(tempFile.toAbsolutePath().toString()))
                    .map(ToApiConversions::responseFromUploadedFiles);
        } catch (IOException e) {
            return Mono.error(e);
        }
    }
}
