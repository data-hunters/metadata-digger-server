package ai.datahunters.md.server.server.photos;

import ai.datahunters.md.server.photos.upload.ArchiveHandler;
import ai.datahunters.md.server.photos.upload.ArchiveHandlerException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static ai.datahunters.md.server.server.testutils.IOHelper.openArchive;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ArchiveHandlerTest {

    private String TEST_DIR = "uploadendpointtest/";

    @Autowired
    ArchiveHandler archiveHandler;

    @Test
    public void fileRecognitionTest() throws IOException, ArchiveHandlerException {
        List<String> expected = Arrays.asList("smile.png");

        Assertions.assertThrows(ArchiveHandlerException.class,
                () -> archiveHandler.probeContentAndUnarchive(openArchive(TEST_DIR + "test_file.zip")));
        Assertions.assertThrows(ArchiveHandlerException.class,
                () -> archiveHandler.probeContentAndUnarchive(openArchive(TEST_DIR + "CORRUPTED_ZIP.zip")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive(TEST_DIR + "ZIP.zip")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive(TEST_DIR + "TAR.tar")));
        Assertions.assertEquals(expected,

                archiveHandler.probeContentAndUnarchive(openArchive(TEST_DIR + "TAR_BZIP2.tar.bz2")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive(TEST_DIR + "TAR_XZ.tar.xz")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive(TEST_DIR + "TAR_GZIP.zip")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive(TEST_DIR + "TAR_BZIP2.zip")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive(TEST_DIR + "ZIP.tar.gz")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive(TEST_DIR + "TAR_GZIP.tar.gz")));
    }

    @Test
    public void multipleFilesExtraction() throws IOException, ArchiveHandlerException {
        List<String> expected = Arrays.asList("smile.png","happy.png");

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive(TEST_DIR + "MZIP.zip")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive(TEST_DIR + "MTAR.tar.bz2")));
    }
}
