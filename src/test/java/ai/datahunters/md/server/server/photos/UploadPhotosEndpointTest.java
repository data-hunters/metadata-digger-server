package ai.datahunters.md.server.server.photos;

import ai.datahunters.md.server.photos.upload.ArchiveHandler;
import ai.datahunters.md.server.photos.upload.FileService;
import org.apache.commons.compress.compressors.CompressorException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static ai.datahunters.md.server.server.testutils.JsonUtils.verifyJsonOutput;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UploadPhotosEndpointTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ArchiveHandler archiveHandler;

    @MockBean
    private FileService service;


    @Test
    public void uploadPhotos() throws IOException {
        Path expectedPath = Files.createTempFile("prefix", "suffix");

        String expectedResponse = "{ \"uploaded_files\": [ \"" + expectedPath.toString() + "\"] }";

        given(service.createFileForUpload()).willReturn(expectedPath);


        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ClassPathResource("uploadendpointtest/test_file.zip"));

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


    @Test
    public void probeArchiveAndExtract() throws IOException, CompressorException {
        List<String> expected = Arrays.asList("smile.png");

        Assertions.assertThrows(IOException.class,
                () -> archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/test_file.zip")));
        Assertions.assertThrows(IOException.class,
                () -> archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/CORRUPTED_ZIP.zip")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/ZIP.zip")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/TAR.tar")));
        Assertions.assertEquals(expected,

                archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/TAR_BZIP2.tar.bz2")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/TAR_XZ.tar.xz")));

        Assertions.assertEquals(expected,
            archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/TAR_GZIP.zip")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/TAR_BZIP2.zip")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/ZIP.tar.gz")));

        Assertions.assertEquals(expected,
                archiveHandler.probeContentAndUnarchive(openArchive("uploadendpointtest/TAR_GZIP.tar.gz")));
    }
    private InputStream openArchive(String file) throws IOException {
        return new BufferedInputStream(new ClassPathResource(file).getInputStream());
    }

}
