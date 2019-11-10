package ai.datahunters.md.server.analytics;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class AnalyticsController {

    private AnalyticsHandler handler;

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/api/v1/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AnalyticsData> getAnalytics() {
        return handler.getAnalytics();
    }


}

