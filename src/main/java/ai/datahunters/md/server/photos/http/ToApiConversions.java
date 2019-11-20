package ai.datahunters.md.server.photos.http;

import ai.datahunters.md.server.photos.search.json.Photo;
import ai.datahunters.md.server.photos.search.json.SearchResponse;
import ai.datahunters.md.server.photos.search.solr.PhotoEntity;
import ai.datahunters.md.server.photos.upload.json.UploadResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class ToApiConversions {
    private ToApiConversions() {
    }
    public static SearchResponse responseFromPhotos(Page<PhotoEntity> modelPhotos) {
        List<Photo> apiPhotos = modelPhotos.stream().map(ToApiConversions::toApiPhoto).collect(Collectors.toList());
        int page = modelPhotos.getNumber();
        long total = modelPhotos.getTotalElements();
        return SearchResponse.builder()
                .photos(apiPhotos)
                .page(page)
                .total(total)
                .build();
    }

    public static UploadResponse responseFromUploadedFiles(List<String> uploadedFiles) {
        return new UploadResponse(uploadedFiles);
    }

    private static Photo toApiPhoto(PhotoEntity entity) {
        return new Photo(
                entity.getId(),
                entity.getBasePath(),
                entity.getFilePath(),
                entity.getFileType(),
                entity.getDirectories(),
                entity.getMetaData()
        );
    }
}
