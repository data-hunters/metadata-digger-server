package ai.datahunters.md.server.server.testutils;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class IOHelper {
    public static InputStream openArchive(String file) throws IOException {
        return new BufferedInputStream(new ClassPathResource(file).getInputStream());
    }

    public static Path createTestDir() throws IOException {
        Path testDir = Paths.get(Long.toUnsignedString(new Random().nextLong()));
        return Files.createDirectory(testDir);
    }
}
