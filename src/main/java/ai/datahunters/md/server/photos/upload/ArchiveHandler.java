package ai.datahunters.md.server.photos.upload;

import ai.datahunters.md.server.photos.upload.filesystem.FileService;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArchiveHandler {

    public ArchiveHandler(@Value("${dirForUploadedFiles}") Path parentDirForFiles, FileService fileService) {
        this.parentDirForFiles = parentDirForFiles;
        this.fileService = fileService;
    }

    public static final String GTAR = "application/x-gtar";
    public static final String TAR = "application/x-tar";
    public static final String ZIP = "application/zip";
    public static final String GZ = "application/gzip";
    public static final String XZ = "application/x-xz";
    public static final String BZ2 = "application/x-bzip2";
    public static final String INVALID_FILE_MSG = "Use valid ZIP (*.zip), GZIP (*.tar.gz), XZ (*.tar.xz), BZIP2 (*.tar.bz2) " +
            "or uncompressed archive (*.tar)";

    private Path parentDirForFiles;
    private FileService fileService;

    Tika tika = new Tika();

    public List<String> probeContentAndUnarchive(InputStream in) throws IOException, ArchiveHandlerException {
        try {
            String type = tika.detect(in);
            if (type.equals(TAR) || type.equals(GTAR) || type.equals(ZIP)) {
                return detectArchiver(in);
            } else if (type.equals(GZ) || type.equals(XZ) || type.equals(BZ2)) {
                return detectCompressor(in);
            } else {
                throw new ArchiveHandlerException("Unsupported file type. " + INVALID_FILE_MSG);
            }
        } catch (CompressorException compEx) {
            throw new ArchiveHandlerException("Error while decompressing. " + INVALID_FILE_MSG, compEx);
        } catch (ArchiveException archEx) {
            throw new ArchiveHandlerException("Error while extracting. Use valid archive, either ZIP or TAR.", archEx);
        }
    }

    private List<String> detectCompressor(InputStream in) throws CompressorException, ArchiveException, IOException {
        return detectArchiver(new CompressorStreamFactory().createCompressorInputStream(in));
    }

    private List<String> detectArchiver(InputStream in) throws ArchiveException, IOException {
        return extract(new ArchiveStreamFactory().createArchiveInputStream(new BufferedInputStream(in)));
    }

    private List<String> extract(ArchiveInputStream archive) throws IOException {
        List<String> uploadedFilesList = new ArrayList<>();
        ArchiveEntry entry;

        Path extractionDir = fileService.createDirForExtraction(parentDirForFiles);
        try {
            while ((entry = archive.getNextEntry()) != null) {
                IOUtils.copy(archive, FileUtils.openOutputStream(extractionDir.resolve(entry.getName()).toFile()));
                uploadedFilesList.add(entry.getName());
            }
        } catch (IOException e) {
            try {
                FileUtils.deleteDirectory(extractionDir.toFile());
            } catch (IOException ex) {
                throw new IOException("I/O exception while extracting. Cleanup unsuccessful", e);
            }
            throw new IOException("I/O exception while writing new files. Extraction failed", e);
        }
        return uploadedFilesList;
    }
}
