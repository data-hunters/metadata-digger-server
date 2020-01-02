package ai.datahunters.md.server.server.photos;

import ai.datahunters.md.server.photos.upload.filesystem.FileService;
import ai.datahunters.md.server.photos.upload.uploadid.UploadId;
import ai.datahunters.md.server.photos.upload.uploadid.UploadIdFactory;
import ai.datahunters.md.server.server.testutils.IOHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import static ai.datahunters.md.server.server.testutils.IOHelper.createTestDir;
import static ai.datahunters.md.server.server.testutils.IOHelper.createTestFile;
import static ai.datahunters.md.server.server.testutils.JsonUtils.verifyJsonOutput;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UploadPhotosEndpointTest {
    @Autowired
    private WebTestClient webTestClient;

    private UploadId uploadId = new UploadId(UUID.fromString("4b858941-f2fb-4d9b-a9cc-42c104e4458b"));

    @MockBean
    private UploadIdFactory uploadIdFactory;

    @MockBean
    private FileService service;

    @Test
    public void uploadPhotos() throws IOException {

        Path testDir = createTestDir();
        Path uploadTempFile = createTestFile();

        String expectedResponse = new IOHelper().readStringFromResource("photosendpointtest/upload_response.json");

        given(uploadIdFactory.build()).willReturn(uploadId);
        given(service.createDirForExtraction(uploadId)).willReturn(testDir);
        given(service.createFileForUpload(uploadId)).willReturn(uploadTempFile);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ClassPathResource("uploadendpointtest/MZIP.zip"));

        webTestClient
                .post()
                .uri("/api/v1/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(respBody ->
                        verifyJsonOutput(respBody.getResponseBody(), expectedResponse)
                );

    }
}
