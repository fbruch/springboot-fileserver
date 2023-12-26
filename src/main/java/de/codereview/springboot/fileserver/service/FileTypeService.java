package de.codereview.springboot.fileserver.service;

import org.apache.tika.Tika;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FileTypeService
{
	private final Tika tika = new Tika();

    private final static MediaTypeRegistry mediaTypeRegistry = MediaTypeRegistry.getDefaultRegistry();

    private final static List<MediaType> xmlTypes = new ArrayList<>();

    static {
        xmlTypes.add(MediaType.APPLICATION_XML);
        xmlTypes.addAll(mediaTypeRegistry.getAliases(MediaType.APPLICATION_XML));
    }

    public String detectMimeType(Path path) throws IOException
	{
		String mimetype = tika.detect(path);
        if ("text/x-web-markdown".equals(mimetype)) { // see issue #1
            mimetype = "text/markdown";
        } else if ("text/x-asciidoc".equals(mimetype)) { // X- is deprecated
            mimetype = "text/asciidoc";
        }
        return mimetype;
    }

    public boolean isTextual(String mimetype) {
        MediaType mediatype = MediaType.parse(mimetype);
        boolean textual = false;
        final List<String> subtypes = Arrays.asList("json", "csv", "html", "text");
        if ("text".equals(mediatype.getType()) || xmlTypes.contains(mediatype)
            || subtypes.contains(mediatype.getSubtype())
        ) {
            textual = true;
        }
        return textual;
    }
}
