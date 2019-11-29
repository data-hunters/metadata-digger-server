package ai.datahunters.md.server.photos.upload.filesystem;

import org.springframework.stereotype.Service;

import javax.annotation.processing.FilerException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public Path createFileForUpload() throws IOException {
        return Files.createTempFile("upload", Instant.now().toString());
    }

    @Override
    public Path createDirForExtraction(Path parrentPath) throws IOException {
        if (Files.notExists(parrentPath)) {
            Files.createDirectory(parrentPath);
        } else if (!Files.isDirectory(parrentPath)) {
            throw new FilerException("Misconfiguration: \"" + parrentPath.toString() +
                    "\" already exists and it is NOT a directory.");
        }
        return Files.createDirectory(parrentPath.resolve(Instant.now().toString()));
    }
}
