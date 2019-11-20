package ai.datahunters.md.server.photos.upload.filesystem;

import ai.datahunters.md.server.photos.upload.FileService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

@Component
public class TemporaryFileService implements FileService {
    @Override
    public Path createFileForUpload() throws IOException {
        Instant now = Instant.now();
        return Files.createTempFile("upload", now.toString());
    }
}
