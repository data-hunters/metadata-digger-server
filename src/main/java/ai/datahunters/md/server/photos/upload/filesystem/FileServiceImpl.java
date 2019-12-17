package ai.datahunters.md.server.photos.upload.filesystem;

import javax.annotation.processing.FilerException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

public class FileServiceImpl implements FileService {
    private Path parentDir;

    public FileServiceImpl(Path parentDir) throws IOException {
        if (Files.notExists(parentDir)) {
            Files.createDirectory(parentDir);
        } else if (!Files.isDirectory(parentDir)) {
            throw new FilerException("Misconfiguration: \"" + parentDir.toString() +
                    "\" already exists and it is NOT a directory.");
        }

        this.parentDir = parentDir;
    }

    @Override
    public Path createFileForUpload(UUID uploadId) throws IOException {
        return Files.createTempFile("upload", uploadId.toString());
    }

    @Override
    public Path createDirForExtraction() throws IOException {
        return Files.createDirectory(parentDir.resolve(Instant.now().toString()));
    }
}
