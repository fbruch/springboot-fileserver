package de.codereview.springboot.fileserver.service.converter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MarkdownHtmlTest
{
    MarkdownHtml service = new MarkdownHtml();

    @Test
    public void convert() throws Exception
    {
        Result result = service.convert("# TEST".getBytes(), "DUMMY");
        assertEquals("<html><head><title>TEST</title></head><body><h1>TEST</h1></body></html>",
            new String(result.getContent()).trim().replaceAll("[\n\r ]", ""));
        assertEquals("TEST", result.getTitle());
    }

}
