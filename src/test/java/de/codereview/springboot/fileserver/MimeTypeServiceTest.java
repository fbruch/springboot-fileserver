package de.codereview.springboot.fileserver;

import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class MimeTypeServiceTest
{
    private MimeTypeService service = new MimeTypeService();

    @Test
    public void detectMimeType() throws Exception
    {
        String type = service.detectMimeType(Paths.get(
            "src/test/resources/box/markup/text-markdown.md"));

        assertEquals(type, "text/markdown");
    }

}
