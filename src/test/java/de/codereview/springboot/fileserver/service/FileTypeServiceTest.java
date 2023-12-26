package de.codereview.springboot.fileserver.service;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;


public class FileTypeServiceTest
{
    private final FileTypeService service = new FileTypeService();

    @Test
    public void detectMimeType() throws Exception
    {
        assertEquals("text/plain", service.detectMimeType(Paths.get(
            "src/test/resources/demo/text-plain.txt")));
        assertEquals("text/html", service.detectMimeType(Paths.get(
            "src/test/resources/demo/text-html.html")));
        assertEquals("application/xml", service.detectMimeType(Paths.get(
            "src/test/resources/demo/data/application-xml.xml")));
        assertEquals("text/markdown", service.detectMimeType(Paths.get(
            "src/test/resources/demo/markup/text-markdown.md")));
        assertEquals("text/asciidoc", service.detectMimeType(Paths.get(
            "src/test/resources/demo/markup/text-asciidoc.adoc")));
        assertEquals("image/jpeg", service.detectMimeType(Paths.get(
            "src/test/resources/demo/media/image-jpeg.jpg")));
        assertEquals("image/gif", service.detectMimeType(Paths.get(
            "src/test/resources/demo/media/image-gif.gif")));
        assertEquals("image/png", service.detectMimeType(Paths.get(
            "src/test/resources/demo/media/image-png.png")));
        assertEquals("image/svg+xml", service.detectMimeType(Paths.get(
            "src/test/resources/demo/media/image-svg.svg")));
    }

    @Test
    public void isTextual() {
        assertTrue(service.isTextual("application/json"));
        assertTrue(service.isTextual("application/csv"));
        assertTrue(service.isTextual("application/xml"));
        assertTrue(service.isTextual("application/text"));
        assertTrue(service.isTextual("text/xml"));
        assertTrue(service.isTextual("text/html"));
        assertTrue(service.isTextual("text/plain"));
        assertTrue(service.isTextual("text/asciidoc"));
        assertTrue(service.isTextual("text/markdown"));

        assertFalse(service.isTextual("image/jpeg"));
        assertFalse(service.isTextual("image/gif"));
        assertFalse(service.isTextual("image/png"));
        assertFalse(service.isTextual("application/mp3"));
        // TODO: mp4
    }

}
