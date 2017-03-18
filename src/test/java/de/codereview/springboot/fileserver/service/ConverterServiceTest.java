package de.codereview.springboot.fileserver.service;

import de.codereview.springboot.fileserver.service.converter.ConverterService;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConverterServiceTest
{
    private ConverterService service = new ConverterService();

    @Test
    public void convert() throws Exception
    {
        assertEquals("<html><head><title>DUMMY</title></head><body><h1>TEST</h1></body></html>",
            new String(service.convert("# TEST".getBytes(),
                "text/markdown", "text/html", "DUMMY"))
                .trim().replaceAll("[\n\r ]", ""));
    }

    @Test
    public void getTargetExtension() throws Exception
    {
        assertEquals(".html", service.getTargetExtension("text/html"));
//        assertEquals(".md", service.getTargetExtension("text/markdown"));
        assertEquals(".md", service.getTargetExtension("text/x-web-markdown"));
//        assertEquals(".adoc", service.getTargetExtension("text/asciidoctor"));
        assertEquals(".jpg", service.getTargetExtension("image/jpeg"));
        assertEquals(".pdf", service.getTargetExtension("application/pdf"));
    }

}
