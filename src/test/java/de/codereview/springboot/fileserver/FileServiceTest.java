package de.codereview.springboot.fileserver;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileServiceTest
{
	static final Path FILE_PATH = Paths.get("src/test/resources/box/media/image-jpeg.jpg");

	private FileService service = new FileService("box", "src/test/resources");

	@Test
	public void testReadFile() throws IOException
	{
		byte[] result = service.readFile(FILE_PATH);
		org.junit.Assert.assertThat((long)result.length,
				org.hamcrest.Matchers.equalTo(Files.size(FILE_PATH)));
 	}
}
