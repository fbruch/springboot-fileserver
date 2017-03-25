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
        String type = service.detectMimeType(Paths.get(
            "src/test/resources/demo/markup/text-markdown.md"));

        assertEquals(type, "text/markdown");
    }

}
