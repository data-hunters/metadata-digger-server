package ai.datahunters.md.server.server.testutils;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class IOHelper {
    public static Path testPath = Paths.get("testfiles");

    public static InputStream openArchive(String file) throws IOException {
        return new BufferedInputStream(new ClassPathResource(file).getInputStream());
    }

    public static Path createTestDir() throws IOException {
        Files.createDirectories(testPath);
        Path testDir = Paths.get(testPath.toString(), Long.toUnsignedString(new Random().nextLong()));
        return Files.createDirectory(testDir);
    }

    public static Path createTestFile() throws IOException {
        Files.createDirectories(testPath);
        return Files.createTempFile(testPath, "temp", "file");
    }

    public String readStringFromResource(String path) throws IOException {
        Path expectedResponseFile = Paths.get(
                getClass().getClassLoader().getResource(path).getPath()
        );

        return new String(Files.readAllBytes(expectedResponseFile), StandardCharsets.UTF_8);
    }
}
