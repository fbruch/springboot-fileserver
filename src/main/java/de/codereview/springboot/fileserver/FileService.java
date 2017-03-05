package de.codereview.springboot.fileserver;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class FileService
{
	private Map<String, Path> boxes;

	public FileService()
	{
		boxes = new HashMap<>();
		boxes.put("box", Paths.get("src/test/resources/box")); // FIXME: externalize configuration
	}

	public Path getBoxPath(String box)
	{
		return boxes.get(box);
	}

	public Set<String> getBoxList()
	{
		return boxes.keySet();
	}

	public Path getFilePath(String box, String path) {
		Path boxPath = getBoxPath(box.intern());
		Path filePath = boxPath.resolve(path);
		return filePath;
	}

	public Stream<Path> listDir(Path dirPath) throws IOException
	{
		Stream<Path> stream = Files.list(dirPath);
		return stream;
	}

	public byte[] readFile(Path filePath) throws IOException
	{
		// TODO: limit file size to read in completely, config threshold
		byte[] bytes = Files.readAllBytes(filePath);
		return bytes;
	}

}
