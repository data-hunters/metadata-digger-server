package ai.datahunters.md.server.infrastructure;

import ai.datahunters.md.server.photos.upload.uploadid.UploadId;
import ai.datahunters.md.server.photos.upload.uploadid.UploadIdFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class UploadIdFactoryConfiguration {
    @Bean
    public UploadIdFactory uploadIdFactory() {
        return () -> new UploadId(UUID.randomUUID());
    }
}
