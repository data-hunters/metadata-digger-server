package ai.datahunters.md.server.infrastructure;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@Configuration
@EnableSolrRepositories
public class SolrConfig {
    @Value("${solr.url default value}")
    private String defaultSolrURL = "http://localhost:8983/solr/";
    @Value("${solr.url}")
    private String solrUrl;

    @Bean
    public SolrClient solrClient() {
        return new Http2SolrClient.Builder(solrUrl).build();
    }

    @Bean
    public SolrOperations solrTemplate() {
        return new SolrTemplate(solrClient());
    }
}
