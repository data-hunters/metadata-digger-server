package ai.datahunters.md.server.photos.upload;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArchiveHandler {
    private static final String GTAR = "application/x-gtar";
    private static final String TAR = "application/x-tar";
    private static final String ZIP = "application/zip";
    private static final String GZ = "application/gzip";
    private static final String XZ = "application/x-xz";
    private static final String BZ2 = "application/x-bzip2";
    private static final String INVALID_FILE_MSG = "Use valid ZIP (*.zip), GZIP (*.tar.gz), XZ (*.tar.xz), BZIP2 (*.tar.bz2) " +
            "or uncompressed archive (*.tar)";

    Tika tika = new Tika();

    public List<String> probeContentAndUnarchive(Path outputDirectory, InputStream in) throws IOException, ArchiveHandlerException {
        try {
            String type = tika.detect(in);
            if (type.equals(TAR) || type.equals(GTAR) || type.equals(ZIP)) {
                return detectArchiver(outputDirectory, in);
            } else if (type.equals(GZ) || type.equals(XZ) || type.equals(BZ2)) {
                return detectCompressor(outputDirectory, in);
            } else {
                throw new ArchiveHandlerException("Unsupported file type. " + INVALID_FILE_MSG);
            }
        } catch (CompressorException compEx) {
            throw new ArchiveHandlerException("Error while decompressing. " + INVALID_FILE_MSG, compEx);
        } catch (ArchiveException archEx) {
            throw new ArchiveHandlerException("Error while extracting. Use valid archive, either ZIP or TAR.", archEx);
        }
    }

    private List<String> detectCompressor(Path outputDirectory, InputStream in) throws CompressorException, ArchiveException, IOException {
        return detectArchiver(outputDirectory, new CompressorStreamFactory().createCompressorInputStream(in));
    }

    private List<String> detectArchiver(Path outputDirectory, InputStream in) throws ArchiveException, IOException {
        return extract(outputDirectory, new ArchiveStreamFactory().createArchiveInputStream(new BufferedInputStream(in)));
    }

    private List<String> extract(Path extractionDir, ArchiveInputStream archive) throws IOException {
        List<String> uploadedFilesList = new ArrayList<>();
        ArchiveEntry entry;

        while ((entry = archive.getNextEntry()) != null) {
            IOUtils.copy(archive, FileUtils.openOutputStream(extractionDir.resolve(entry.getName()).toFile()));
            uploadedFilesList.add(entry.getName());
        }
        return uploadedFilesList;
    }
}
