package de.codereview.springboot.fileserver.service;

import de.codereview.springboot.fileserver.service.converter.ConverterService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConverterServiceTest
{
    ConverterService service = new ConverterService();

    @Test
    public void isConversionAvailable() {
        assertTrue(service.isConversionAvailable("text/markdown", "text/html"));
        assertFalse(service.isConversionAvailable("text/web-x-markdown", "text/html"));
    }

    @Test
    public void getTargetExtension() throws Exception
    {
        assertEquals(".html", service.getTargetExtension("text/html"));
//        assertEquals(".md", service.getTargetExtension("text/markdown"));
        assertEquals(".md", service.getTargetExtension("text/x-web-markdown"));

        assertEquals(".jpg", service.getTargetExtension("image/jpeg"));
        assertEquals(".pdf", service.getTargetExtension("application/pdf"));
    }

}
