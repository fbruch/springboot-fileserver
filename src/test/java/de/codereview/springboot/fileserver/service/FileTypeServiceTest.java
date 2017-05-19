package de.codereview.springboot.fileserver.service;

import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class FileTypeServiceTest
{
    private FileTypeService service = new FileTypeService();

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
    }

}
