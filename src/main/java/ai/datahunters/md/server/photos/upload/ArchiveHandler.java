package ai.datahunters.md.server.photos.upload;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.dump.UnrecognizedFormatException;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class ArchiveHandler {
    public static final String GTAR = "application/x-gtar";
    public static final String TAR = "application/x-tar";
    public static final String ZIP = "application/zip";
    public static final String GZ = "application/gzip";
    public static final String XZ = "application/x-xz";
    public static final String BZ2 = "application/x-bzip2";

    @Value("${dirForUploadedFiles}")
    String parentDirForFiles;


    Tika tika = new Tika();
    public List<String> probeContentAndUnarchive(InputStream in) throws IOException {
        try {
            String type = tika.detect(in);
            if (type.equals(TAR) || type.equals(GTAR) || type.equals(ZIP)) {
                return detectArchiver(in);
            } else if (type.equals(GZ) || type.equals(XZ) || type.equals(BZ2)) {
                return detectCompressor(in);
            } else {
                throw new UnrecognizedFormatException();
            }
        } catch (CompressorException compEx) {
            throw new IOException("Error while decompressing. Use valid ZIP (*.zip), GZIP (*.tar.gz), XZ (*.tar.xz), BZIP2 (*.tar.bz2) or uncompressed archive (*.tar)");
        } catch (ArchiveException archEx) {
            throw new IOException("Error while extracting. Use valid archive, either ZIP or TAR.");
        }
    }


    private List<String> detectCompressor(InputStream in) throws CompressorException, ArchiveException, IOException {
        return detectArchiver(new CompressorStreamFactory().createCompressorInputStream(in));
    }

    private List<String> detectArchiver(InputStream in) throws ArchiveException, IOException {
        return unarchive(new ArchiveStreamFactory().createArchiveInputStream(new BufferedInputStream(in)));
    }

    private List<String> unarchive(ArchiveInputStream archive) throws IOException {
        List<String> uploadedFilesList = new ArrayList<>();
        ArchiveEntry entry;
        Boolean success = false;
        File target = new File(parentDirForFiles + Instant.now().toString());
        try {
            FileUtils.forceMkdir(target);
            while ((entry = archive.getNextEntry()) != null) {
                IOUtils.copy(archive, FileUtils.openOutputStream(new File(target + File.separator +  entry.getName())));
                uploadedFilesList.add(entry.getName());
            }
            success = true;
        } catch (IOException e) {
            throw new IOException("I/O exception while writing new files. Extraction failed");
        } finally {
            if (success == false) {
                try {
                    FileUtils.deleteDirectory(target);
                } catch (IOException e) {
                   throw new IOException("I/O exception while extracting Cleanup unsuccessful");
                }
            }
        }
        return uploadedFilesList;
    }
}
