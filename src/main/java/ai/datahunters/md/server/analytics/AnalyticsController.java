package ai.datahunters.md.server.analytics;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

@AllArgsConstructor
@RestController
public class AnalyticsController {

    private AnalyticsHandler handler;

    @GetMapping(value = "/api/v1/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AnalyticsData> getAnalytics() throws ExecutionException, InterruptedException {
        return handler.getAnalytics();
    }


}

