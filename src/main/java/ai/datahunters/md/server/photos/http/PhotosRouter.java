package ai.datahunters.md.server.photos.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class PhotosRouter {
    @Bean
    public RouterFunction<ServerResponse> photoRoutes(PhotosHandler handler) {
        return RouterFunctions.route(RequestPredicates.POST("/api/v1/photos"), handler::search);
    }
}
