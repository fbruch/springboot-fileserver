package de.codereview.springboot.fileserver.service.converter;

import org.junit.Test;

import static org.junit.Assert.*;

public class AsciidocHtmlTest
{
    AsciidocHtml service = new AsciidocHtml("images");

    @Test
    public void convert() throws Exception
    {
        Result result = service.convert("# TEST".getBytes(), "DUMMY");
        assertTrue(new String(result.getContent()).contains("<title>TEST</title>"));
        assertEquals("TEST", result.getTitle());
    }

}
