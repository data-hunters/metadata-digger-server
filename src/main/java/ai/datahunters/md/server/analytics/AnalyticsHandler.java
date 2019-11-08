package ai.datahunters.md.server.analytics;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

@AllArgsConstructor
@Component
public class AnalyticsHandler {

    private AnalyticsService service;

    public Mono<AnalyticsData> getAnalytics() throws ExecutionException, InterruptedException {
            return Mono.just(new AnalyticsData(service.photoCount().get()));
    }
}
