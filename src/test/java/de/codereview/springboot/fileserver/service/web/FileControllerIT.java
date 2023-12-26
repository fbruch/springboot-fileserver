package de.codereview.springboot.fileserver.service.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class FileControllerIT
{
    private static final String MARKDOWN_FILE_PATH = "/app/src/test/resources/demo/markup/text-markdown.md";
    private static final String ASCIIDOC_FILE_PATH = "/app/src/test/resources/demo/markup/text-asciidoc.adoc";

    @Autowired
    private TestRestTemplate restTemplate;

//    @MockBean
//    private FileService fileService;

    @BeforeEach
    public void setup() {
//        given(fileService.getVehicleDetails("123")
//        ).willReturn(new VehicleDetails("Honda", "Civic"));
    }

    @Test
    public void markdown() {
        ResponseEntity<String> entity = restTemplate.getForEntity(
            MARKDOWN_FILE_PATH, String.class);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        MediaType contentType = entity.getHeaders().getContentType();
        assertThat(contentType.getType()).isEqualTo("text");
        assertThat(contentType.getSubtype()).isEqualTo("markdown");
        assertThat(contentType.getCharset()).isEqualTo(StandardCharsets.UTF_8);
    }

    @Test
    public void asciidoc() {
        ResponseEntity<String> entity = restTemplate.getForEntity(
            ASCIIDOC_FILE_PATH, String.class);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        MediaType contentType = entity.getHeaders().getContentType();
        assertThat(contentType.getType()).isEqualTo("text");
        assertThat(contentType.getSubtype()).isEqualTo("asciidoc");
        assertThat(contentType.getCharset()).isEqualTo(StandardCharsets.UTF_8);
    }

    @Test
    public void markdownAsHtml() {
        convertedToHtml(MARKDOWN_FILE_PATH);
    }

    @Test
    public void asciidocAsHtml() {
        convertedToHtml(ASCIIDOC_FILE_PATH);
    }

    void convertedToHtml(String asciidocFilePath)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.TEXT_HTML_VALUE);
        ResponseEntity<String> entity = restTemplate.exchange(
            asciidocFilePath, HttpMethod.GET,
            new HttpEntity<>(headers), String.class);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        MediaType contentType = entity.getHeaders().getContentType();
        assertThat(contentType.getType()).isEqualTo("text");
        assertThat(contentType.getSubtype()).isEqualTo("html");
        assertThat(contentType.getCharset()).isEqualTo(StandardCharsets.UTF_8);
    }
}
