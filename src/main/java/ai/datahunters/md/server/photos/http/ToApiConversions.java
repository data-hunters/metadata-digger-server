package ai.datahunters.md.server.photos.http;

import ai.datahunters.md.server.photos.http.json.SearchResponse;
import ai.datahunters.md.server.photos.solr.PhotoEntity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ToApiConversions {
    public static SearchResponse responseFromPhotos(List<PhotoEntity> modelPhotos) {
        List<Photo> apiPhotos = modelPhotos.stream().map(ToApiConversions::toApiPhoto).collect(Collectors.toList());
        return new SearchResponse(apiPhotos);
    }

    private static Photo toApiPhoto(PhotoEntity entity) {
        return new Photo(
                entity.getId(),
                entity.getFileType(),
                entity.getDirectories()
        );
    }
}
