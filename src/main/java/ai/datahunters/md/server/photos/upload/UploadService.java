package ai.datahunters.md.server.photos.upload;

import ai.datahunters.md.server.photos.http.ToApiConversions;
import lombok.AllArgsConstructor;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UploadService {
    FileService fileService;

    public Mono<UploadResponse> handleUpload(FilePart filePart) {
        try {
            Path tempFile = fileService.createFileForUpload();

            AsynchronousFileChannel channel = AsynchronousFileChannel.open(tempFile, StandardOpenOption.WRITE);

            return DataBufferUtils.write(filePart.content(), channel, 0)
                    .doOnComplete(() -> System.out.println("uploaded"))
                    .collect(Collectors.counting())
                    .map(count -> buildResultList(tempFile.toAbsolutePath().toString()))
                    .map(ToApiConversions::responseFromUploadedFiles);
        } catch (IOException e) {
            return Mono.error(e);
        }
    }

    private List<String> buildResultList(String file) {
        List<String> l = new ArrayList<>();
        l.add(file);
        return l;
    }
}
