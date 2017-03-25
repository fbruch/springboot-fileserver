package de.codereview.springboot.fileserver.service;

import de.codereview.springboot.fileserver.service.converter.AsciidocHtml;
import de.codereview.springboot.fileserver.service.converter.ConverterService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.*;

public class ConverterServiceTest
{
    ConverterService service;

    @Before
    public void setUp() throws Exception
    {
        AsciidocHtml asciidoc = Mockito.mock(AsciidocHtml.class);
        BDDMockito.given(asciidoc.getSource()).willReturn("text/asciidoc");
        BDDMockito.given(asciidoc.getTarget()).willReturn("text/html");
        service = new ConverterService(asciidoc);
    }

    @Test
    public void isConversionAvailable() {
        assertTrue(service.isConversionAvailable("text/asciidoc", "text/html"));
        assertFalse(service.isConversionAvailable("text/x-asciidoc", "text/html"));
        assertTrue(service.isConversionAvailable("text/markdown", "text/html"));
        assertFalse(service.isConversionAvailable("text/web-x-markdown", "text/html"));
    }

    @Test
    public void getTargetExtension() throws Exception
    {
        assertEquals(".html", service.getTargetExtension("text/html"));
//        assertEquals(".md", service.getTargetExtension("text/markdown"));
        assertEquals(".md", service.getTargetExtension("text/x-web-markdown"));
        assertEquals(".asciidoc", service.getTargetExtension("text/x-asciidoc"));
//        assertEquals(".adoc", service.getTargetExtension("text/asciidoctor"));

        assertEquals(".jpg", service.getTargetExtension("image/jpeg"));
        assertEquals(".pdf", service.getTargetExtension("application/pdf"));
    }

}
