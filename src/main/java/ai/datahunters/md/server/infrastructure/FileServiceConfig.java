package ai.datahunters.md.server.infrastructure;

import ai.datahunters.md.server.photos.indexing.filesystem.FileService;
import ai.datahunters.md.server.photos.indexing.filesystem.FileServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Paths;

@Configuration
public class FileServiceConfig {
    @Value("${dirForUploadedFiles}")
    private String directoryName;

    @Bean
    public FileService fileService() throws IOException {
        return new FileServiceImpl(Paths.get(directoryName));
    }
}
