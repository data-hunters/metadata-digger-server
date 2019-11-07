package ai.datahunters.md.server.analytics;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@AllArgsConstructor
@Component
public class AnalyticsHandler {

    private AnalyticsService service;

    public ResponseEntity getAnalytics()  {
        try {
            return ResponseEntity.ok(new AnalyticsData(service.photoCount()));
        }  catch (ExecutionException e) {
            return ResponseEntity.ok("{" +
                            "\"error\" : \"cannot connect to Solr\"  }");
        } catch (InterruptedException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
