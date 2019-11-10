package ai.datahunters.md.server.analytics;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class AnalyticsHandler {

    private AnalyticsService service;

    public Mono<AnalyticsData> getAnalytics() {
            return Mono.fromFuture(service.photoCount()).map(AnalyticsData::new);
    }
}
