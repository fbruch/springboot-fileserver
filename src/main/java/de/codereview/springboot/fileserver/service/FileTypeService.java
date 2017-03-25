package de.codereview.springboot.fileserver.service;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class FileTypeService
{
	private Tika tika = new Tika();

	public String detectMimeType(Path path) throws IOException
	{
		String mimetype = tika.detect(path);
        if ("text/x-web-markdown".equals(mimetype)) { // see #1
            mimetype = "text/markdown";
        } else if ("text/x-asciidoc".equals(mimetype)) { // X- is deprecated
            mimetype = "text/asciidoc";
        }
        return mimetype;
	}
}
