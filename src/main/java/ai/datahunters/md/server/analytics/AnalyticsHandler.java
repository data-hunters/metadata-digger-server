package ai.datahunters.md.server.analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@Component
public class AnalyticsHandler {

    private AnalyticsService service;

    @Autowired
    public AnalyticsHandler(AnalyticsService service) {
        this.service = service;
    }

    public Mono<ServerResponse> getAnalytics()  {
        try {
            return ok().contentType(MediaType.APPLICATION_JSON).
                    body(fromValue(new AnalyticsData(service.photoCount())));
        }  catch (ExecutionException e) {
            return ok().contentType(MediaType.APPLICATION_JSON)
                    .body(fromValue("{" +
                            "\"error\" : \"cannot connect to Solr\"" +
                            "               }"));
        } catch (InterruptedException e) {
            return Mono.error(e);
        }
    }
}
