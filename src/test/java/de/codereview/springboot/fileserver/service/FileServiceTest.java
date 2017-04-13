package de.codereview.springboot.fileserver.service;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class FileServiceTest
{
	static final Path FILE_PATH = Paths.get("src/test/resources/demo/media/image-jpeg.jpg");

	private FileService service;

	@Before
    public void setUp() {
	    FileServiceConfig config = new FileServiceConfig();
	    config.getRoots().add(new FileServiceConfig.Root("demo", "src/test/resources"));
	    service = new FileService(config);
    }

	@Test
	public void testReadFile() throws IOException
	{
		byte[] result = service.readFile(FILE_PATH);
		org.junit.Assert.assertThat((long)result.length,
				org.hamcrest.Matchers.equalTo(Files.size(FILE_PATH)));
 	}
}
