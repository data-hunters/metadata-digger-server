package ai.datahunters.md.server.server.photos;

import ai.datahunters.md.server.photos.upload.ArchiveHandler;
import ai.datahunters.md.server.photos.upload.ArchiveHandlerException;
import ai.datahunters.md.server.photos.upload.filesystem.FileService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import static ai.datahunters.md.server.server.testutils.IOHelper.openArchive;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ExtractedFilesValidation {
    private String TEST_DIR = "uploadendpointtest/";
  
    FileService fileService = mock(FileService.class);
    ArchiveHandler archiveHandler = new ArchiveHandler(Paths.get("mock"),fileService);

   @Test
    public void integrityValidation() throws IOException, ArchiveHandlerException {
       Path testDir = createTestDir();
       given(fileService.createDirForExtraction(any(Path.class))).willReturn(testDir);

       archiveHandler.probeContentAndUnarchive(openArchive(TEST_DIR + "MZIP.zip"));

       Assertions.assertEquals("9e537fd87c06667e5d87679e6300092b3383bf5c3685b96c36639157f776379b",
               DigestUtils.sha256Hex(new FileInputStream(testDir.resolve("happy.png").toFile())));
       Assertions.assertEquals("dbe900e9465c658b9b4a7b4073ac9eac05cc52da6ee90dec80e62c4803c22955",
               DigestUtils.sha256Hex(new FileInputStream(testDir.resolve("smile.png").toFile())));
       FileUtils.deleteDirectory(testDir.toFile());
   }
  
   @Test
   public void checkDirCleanup() throws IOException {
      Path testDir = createTestDir();
      given(fileService.createDirForExtraction(any(Path.class))).willReturn(testDir);

      Assertions.assertThrows(IOException.class, () ->
              archiveHandler.probeContentAndUnarchive(openArchive(TEST_DIR + "MTAR_WITH_INVALID_ENTRY.tar")));

      Assertions.assertFalse(testDir.toFile().exists());

   }
  
   private Path createTestDir() throws IOException {
      Path testDir = Paths.get(Long.toUnsignedString(new Random().nextLong()));
      return Files.createDirectory(testDir);
   }
}
