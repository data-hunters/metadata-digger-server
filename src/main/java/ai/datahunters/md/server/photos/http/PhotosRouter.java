package ai.datahunters.md.server.photos.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class PhotosRouter {
    @Bean
    public RouterFunction<ServerResponse> count(PhotosHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("/photos/count"), handler::count);

    }

    @Bean
    public RouterFunction<ServerResponse> jpgs(PhotosHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("/photos/count/jpg"), handler::getJpgs);
    }
}
