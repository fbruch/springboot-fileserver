package de.codereview.springboot.fileserver.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class FileServiceTest
{
    static final String BOX = "DUMMY";

    private FileService service;

    private FileTypeService fileTypeService;

	@Before
    public void setUp() {
        fileTypeService = Mockito.mock(FileTypeService.class);
	    FileServiceConfig config = new FileServiceConfig();
	    config.getRoots().add(new FileServiceConfig.Root(BOX, "src/test/resources/demo"));
	    service = new FileService(config, fileTypeService);
    }

 	@Test
    public void boxRootdirectoryNonRecursive() throws IOException
    {
        Mockito.when(fileTypeService.detectMimeType(Mockito.any())).thenReturn("text/plain");
        Mockito.when(fileTypeService.isTextual(Mockito.any())).thenReturn(true);
        FileResult result = service.getFile(BOX, "", false);
        assertThat(result.getFilename()).isEqualTo("");
        assertThat(result.getBox()).isEqualTo(BOX);
        assertThat(result.getEncoding()).isNull();
        assertThat(result.getParentPath()).isNull();
        assertThat(result.getLanguage()).isNull();
        assertThat(result.isTextual()).isFalse();
        assertThat(result.isDirectory()).isTrue();
        assertThat(result.getHeader().get(HttpHeaders.CONTENT_TYPE)).isNull();
    }

 	@Test
    public void directoryNonRecursive() throws IOException
    {
        Mockito.when(fileTypeService.detectMimeType(Mockito.any())).thenReturn("text/plain");
        Mockito.when(fileTypeService.isTextual(Mockito.any())).thenReturn(true);
        FileResult result = service.getFile(BOX, "src", false);
        assertThat(result.getFilename()).isEqualTo("src");
        assertThat(result.getBox()).isEqualTo(BOX);
        assertThat(result.getEncoding()).isNull();
        assertThat(result.getParentPath()).isEqualTo("");
        assertThat(result.getLanguage()).isNull();
        assertThat(result.isTextual()).isFalse();
        assertThat(result.isDirectory()).isTrue();
        assertThat(result.getHeader().get(HttpHeaders.CONTENT_TYPE)).isNull();
    }

    @Test
    public void textualFile() throws IOException
    {
        String MIME_TYPE = "text/csv";
        Mockito.when(fileTypeService.detectMimeType(Mockito.any())).thenReturn(MIME_TYPE);
        Mockito.when(fileTypeService.isTextual(Mockito.any())).thenReturn(true);
        FileResult result = service.getFile(BOX, "data/subdir/text-csv.csv", false);
        assertThat(result.getFilename()).isEqualTo("text-csv.csv");
        assertThat(result.getBox()).isEqualTo(BOX);
        assertThat(result.getEncoding()).isEqualTo(Charset.defaultCharset().name());
        assertThat(result.getParentPath()).isEqualTo("data/subdir");
        assertThat(result.getLanguage()).isNull();
        assertThat(result.isTextual()).isTrue();
        assertThat(result.isDirectory()).isFalse();
        assertThat(result.getHeader().get(HttpHeaders.CONTENT_TYPE)).isEqualTo(MIME_TYPE);
    }

    @Test
    public void binaryFile() throws IOException
    {
        String MIME_TYPE = "image/jpeg";
        Mockito.when(fileTypeService.detectMimeType(Mockito.any())).thenReturn(MIME_TYPE);
        Mockito.when(fileTypeService.isTextual(Mockito.any())).thenReturn(false);
        FileResult result = service.getFile(BOX, "media/image-jpeg.jpg", false);
        assertThat(result.getFilename()).isEqualTo("image-jpeg.jpg");
        assertThat(result.getBox()).isEqualTo(BOX);
        assertThat(result.getEncoding()).isNull();
        assertThat(result.getParentPath()).isEqualTo("media");
        assertThat(result.getLanguage()).isNull();
        assertThat(result.isTextual()).isFalse();
        assertThat(result.isDirectory()).isFalse();
        assertThat(result.getHeader().get(HttpHeaders.CONTENT_TYPE)).isEqualTo("image/jpeg");
    }

    @Test
    public void readFile() throws IOException
    {
        final Path FILE_PATH = Paths.get("src/test/resources/demo/media/image-jpeg.jpg");
        byte[] result = service.readFile(FILE_PATH);
        org.junit.Assert.assertThat((long)result.length,
            org.hamcrest.Matchers.equalTo(Files.size(FILE_PATH)));
    }

}
