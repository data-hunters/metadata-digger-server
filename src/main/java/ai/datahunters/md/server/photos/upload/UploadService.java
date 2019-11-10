package ai.datahunters.md.server.photos.upload;

import ai.datahunters.md.server.photos.http.ToApiConversions;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UploadService {
    public Mono<UploadResponse> handleFileParts(Flux<Part> parts) {
        return parts.filter(part -> part instanceof FilePart)
                .map(part -> (FilePart) part)
                .flatMap(filePart -> Flux.concat(handleUpload(filePart)))
                .collect(Collectors.toList())
                .map(ToApiConversions::responseFromUploadedFiles);

    }

    private Mono<String> handleUpload(FilePart filePart) {
        try {
            String fileName = UUID.randomUUID().toString();
            Path tempFile = Files.createTempFile("upload", fileName);

            AsynchronousFileChannel channel = AsynchronousFileChannel.open(tempFile, StandardOpenOption.WRITE);

            return DataBufferUtils.write(filePart.content(), channel, 0)
                    .doOnComplete(() -> System.out.println("uploaded"))
                    .collect(Collectors.counting())
                    .map(count -> "transfered" + count + "bytes");
        } catch (IOException e) {
            return Mono.error(e);
        }
    }
}
