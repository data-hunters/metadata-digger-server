package ai.datahunters.md.server.infrastructure;

import ai.datahunters.md.server.photos.indexing.uploadid.IndexingJobId;
import ai.datahunters.md.server.photos.indexing.uploadid.IndexingJobIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class UploadIdFactoryConfiguration {
    @Bean
    public IndexingJobIdGenerator uploadIdFactory() {
        return () -> new IndexingJobId(UUID.randomUUID());
    }
}
