package ai.datahunters.md.server.server.photos;

import ai.datahunters.md.server.photos.upload.ArchiveHandler;
import ai.datahunters.md.server.photos.upload.ArchiveHandlerException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ArchiveHandlerTest {

    @Autowired
    ArchiveHandler archiveHandler;

    @Test
    public void fileRecognitionTest() throws IOException, ArchiveHandlerException {
        List<String> expected = Arrays.asList("smile.png");

        Assertions.assertThrows(ArchiveHandlerException.class,
                () -> archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/test_file.zip")));
        Assertions.assertThrows(ArchiveHandlerException.class,
                () -> archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/CORRUPTED_ZIP.zip")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/ZIP.zip")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/TAR.tar")));
        Assertions.assertEquals(expected,

                archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/TAR_BZIP2.tar.bz2")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/TAR_XZ.tar.xz")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/TAR_GZIP.zip")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/TAR_BZIP2.zip")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/ZIP.tar.gz")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/TAR_GZIP.tar.gz")));
    }

    @Test
    public void multipleFilesExtraction() throws IOException, ArchiveHandlerException {
        List<String> expected = Arrays.asList("smile.png", "happy.png");

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/MZIP.zip")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/MTAR.tar.bz2")));
    }

    private InputStream openArchive(String file) throws IOException {
        return new BufferedInputStream(new ClassPathResource(file).getInputStream());
    }
}
