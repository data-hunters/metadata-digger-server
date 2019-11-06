package ai.datahunters.md.server.analytics;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;


@Configuration
public class AnalyticsRouter {

    @Bean
    public RouterFunction route(AnalyticsHandler handler) {

            return RouterFunctions.route(RequestPredicates.GET("/api/v1/analytics"), request -> handler.getAnalytics());


    }

}

