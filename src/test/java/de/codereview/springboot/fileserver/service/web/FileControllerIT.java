package de.codereview.springboot.fileserver.service.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileControllerIT
{
    @Autowired
    private TestRestTemplate restTemplate;

//    @MockBean
//    private FileService fileService;

    @Before
    public void setup() {
//        given(fileService.getVehicleDetails("123")
//        ).willReturn(new VehicleDetails("Honda", "Civic"));
    }

    @Test
    public void markdown() {
        ResponseEntity<String> entity = restTemplate.getForEntity(
            "/fs/demo/markup/text-markdown.md", String.class);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        MediaType contentType = entity.getHeaders().getContentType();
        assertThat(contentType.getType()).isEqualTo("text");
        assertThat(contentType.getSubtype()).isEqualTo("markdown");
        assertThat(contentType.getCharset()).isEqualTo(Charset.forName("UTF8"));
    }

    @Test
    public void asciidoc() {
        ResponseEntity<String> entity = restTemplate.getForEntity(
            "/fs/demo/markup/text-asciidoc.adoc", String.class);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        MediaType contentType = entity.getHeaders().getContentType();
        assertThat(contentType.getType()).isEqualTo("text");
        assertThat(contentType.getSubtype()).isEqualTo("asciidoc");
        assertThat(contentType.getCharset()).isEqualTo(Charset.forName("UTF8"));
    }
}
