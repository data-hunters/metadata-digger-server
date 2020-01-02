package ai.datahunters.md.server.infrastructure;

import ai.datahunters.md.server.photos.indexing.IndexingService;
import ai.datahunters.md.server.photos.indexing.extract.ExtractService;
import ai.datahunters.md.server.photos.indexing.upload.UploadService;
import ai.datahunters.md.server.photos.indexing.uploadid.UploadIdFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IndexingServiceConfiguration {
    @Bean
    public IndexingService indexingService(UploadService uploadService, ExtractService extractService, UploadIdFactory uploadIdFactory) {
        return new IndexingService(uploadService, extractService, uploadIdFactory);
    }
}
