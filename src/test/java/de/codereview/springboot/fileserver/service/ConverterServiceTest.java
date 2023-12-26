package de.codereview.springboot.fileserver.service;

import de.codereview.fileserver.api.v1.Converter;
import de.codereview.springboot.fileserver.service.plugin.PluginProperties;
import de.codereview.springboot.fileserver.service.plugin.converter.AsciidocHtml;
import de.codereview.springboot.fileserver.service.plugin.ConverterService;
import de.codereview.springboot.fileserver.service.plugin.converter.MarkdownHtml;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("dev")
public class ConverterServiceTest
{
    ConverterService service;

    @BeforeEach
    public void setUp() {
        AsciidocHtml asciidoc = Mockito.mock(AsciidocHtml.class);
        BDDMockito.given(asciidoc.getSource()).willReturn("text/asciidoc");
        BDDMockito.given(asciidoc.getTarget()).willReturn("text/html");
        List<Converter> converters = new ArrayList<>();
        converters.add(new MarkdownHtml());
        service = new ConverterService(asciidoc, converters, new PluginProperties());
    }

    @Test
    public void isConversionAvailable() {
        assertTrue(service.isConversionAvailable("text/asciidoc", "text/html"));
        assertFalse(service.isConversionAvailable("text/x-asciidoc", "text/html"));
        assertTrue(service.isConversionAvailable("text/markdown", "text/html"));
        assertFalse(service.isConversionAvailable("text/web-x-markdown", "text/html"));
    }

    @Test
    public void getTargetExtension() {
        assertEquals(".html", service.getTargetExtension("text/html"));
//        assertEquals(".md", service.getTargetExtension("text/markdown"));
        assertEquals(".md", service.getTargetExtension("text/x-web-markdown"));
        assertEquals(".asciidoc", service.getTargetExtension("text/x-asciidoc"));
//        assertEquals(".adoc", service.getTargetExtension("text/asciidoctor"));

        assertEquals(".jpg", service.getTargetExtension("image/jpeg"));
        assertEquals(".pdf", service.getTargetExtension("application/pdf"));
    }

}
