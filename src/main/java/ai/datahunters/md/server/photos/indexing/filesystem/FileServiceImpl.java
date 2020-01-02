package ai.datahunters.md.server.photos.indexing.filesystem;

import ai.datahunters.md.server.photos.indexing.uploadid.UploadId;

import javax.annotation.processing.FilerException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileServiceImpl implements FileService {
    private final String UPLOAD_DIRECTORY_NAME = "upload";
    private final String EXTRACT_DIRECTORY_NAME = "extract";
    private Path uploadDir;
    private Path extractionDir;

    public FileServiceImpl(Path parentDir) throws IOException {
        createDirectory(parentDir);

        this.uploadDir = parentDir.resolve(UPLOAD_DIRECTORY_NAME);
        createDirectory(this.uploadDir);

        this.extractionDir = parentDir.resolve(EXTRACT_DIRECTORY_NAME);
        createDirectory(this.extractionDir);
    }

    @Override
    public Path createFileForUpload(UploadId uploadId) throws IOException {
        return Files.createTempFile(uploadDir, "upload", uploadId.getName());
    }

    @Override
    public Path createDirForExtraction(UploadId uploadId) throws IOException {
        return Files.createDirectory(extractionDir.resolve(uploadId.getName()));
    }

    private void createDirectory(Path directory) throws IOException {
        if (Files.notExists(directory)) {
            Files.createDirectory(directory);
        } else if (!Files.isDirectory(directory)) {
            throw new FilerException("Misconfiguration: \"" + directory.toString() +
                    "\" already exists and it is NOT a directory.");
        }
    }
}
