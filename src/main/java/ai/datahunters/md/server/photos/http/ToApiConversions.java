package ai.datahunters.md.server.photos.http;

import ai.datahunters.md.server.photos.indexing.IndexingEvent;
import ai.datahunters.md.server.photos.indexing.extract.FilesExtracted;
import ai.datahunters.md.server.photos.indexing.json.FileUploadedResponse;
import ai.datahunters.md.server.photos.indexing.json.FilesExtractedResponse;
import ai.datahunters.md.server.photos.indexing.json.IndexingStartedResponse;
import ai.datahunters.md.server.photos.indexing.upload.FileUploaded;
import ai.datahunters.md.server.photos.search.json.Photo;
import ai.datahunters.md.server.photos.search.json.SearchResponse;
import ai.datahunters.md.server.photos.search.solr.PhotoEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.codec.ServerSentEvent;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
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

    public static IndexingStartedResponse responseFromUploadResult(FileUploaded result) {
        return new IndexingStartedResponse(result.getIndexingJobId().getId());
    }

    public static ServerSentEvent<String> indexingEventResponse(IndexingEvent event) {
        ObjectMapper Obj = new ObjectMapper();
        String serailziedEvent = null;

        try {
            if (event instanceof FileUploaded) {
                serailziedEvent = Obj.writeValueAsString(fileUploadedResponse((FileUploaded) event));

            } else if (event instanceof FilesExtracted) {
                serailziedEvent = Obj.writeValueAsString(filesExtractedResponse((FilesExtracted) event));
            } else {
                throw new RuntimeException("Cannot serialize event" + event);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ServerSentEvent.<String>builder()
                .id(UUID.randomUUID().toString())
                .event("IndexingEvent")
                .data(serailziedEvent)
                .build();
    }

    private static FileUploadedResponse fileUploadedResponse(FileUploaded fileUploaded) {
        return new FileUploadedResponse(fileUploaded.getIndexingJobId().getId());
    }

    private static FilesExtractedResponse filesExtractedResponse(FilesExtracted filesExtracted) {
        List<String> fileNames = filesExtracted.getExtractedFilesPaths().stream()
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
        return new FilesExtractedResponse(filesExtracted.getIndexingJobId().getId(), fileNames);
    }

    private static Photo toApiPhoto(PhotoEntity entity) {
        return new Photo(
                entity.getId(),
                entity.getBasePath(),
                entity.getFilePath(),
                entity.getFileType(),
                entity.getDirectoryNames(),
                entity.getTagNames(),
                entity.getLabels(),
                entity.getMetaData()
        );
    }
}
