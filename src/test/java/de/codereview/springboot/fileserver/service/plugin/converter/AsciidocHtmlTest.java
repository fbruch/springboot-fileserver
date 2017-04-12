package de.codereview.springboot.fileserver.service.plugin.converter;

import de.codereview.springboot.fileserver.service.plugin.ConverterResult;
import de.codereview.springboot.fileserver.service.plugin.PluginProperties;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AsciidocHtmlTest
{
    AsciidocHtml service;

    @Before
    public void setUp() {
        PluginProperties properties = new PluginProperties();
        properties.getAsciidoctor().put("images-dir", "images");
        service = new AsciidocHtml(properties);
    }

    @Test
    public void convert() throws Exception
    {
        ConverterResult result = service.convert("# TEST".getBytes(), "DUMMY");
        assertTrue(new String(result.getContent()).contains("<title>TEST</title>"));
        assertEquals("TEST", result.getTitle());
    }

}
