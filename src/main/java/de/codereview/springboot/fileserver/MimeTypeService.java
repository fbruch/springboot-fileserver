package de.codereview.springboot.fileserver;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class MimeTypeService
{
	private Tika tika = new Tika();

	public String detectMimeType(Path path) throws IOException
	{
		return tika.detect(path);
	}
}
