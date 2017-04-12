package de.codereview.springboot.fileserver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class FileService
{
    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    private Map<String, Path> boxes;

    @Autowired
    public FileService(@Value("${fileserver.box.root.name}") String box,
                       @Value("${fileserver.box.root.path}") String path)
    {
        boxes = new HashMap<>();
        boxes.put(box, Paths.get(path));
    }

    public Path getBoxPath(String box)
    {
        return boxes.get(box);
    }

    public Set<String> getBoxList()
    {
        return boxes.keySet();
    }

    public Path getFilePath(String box, String path)
    {
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
        // TODO: limit file size to read in completely, config threshold, otherwise stream...
        byte[] bytes = Files.readAllBytes(filePath);
        return bytes;
    }

    public Map<String, String> getFileMetadata(Path filePath)
    {
        Map<String, String> result = new HashMap<>();
        try {
            FileTime lastModifiedTime = Files.getLastModifiedTime(filePath);
            String lastModified = DateTimeFormatter.RFC_1123_DATE_TIME
                .withZone(ZoneOffset.UTC).format(lastModifiedTime.toInstant());
            result.put(HttpHeaders.LAST_MODIFIED, lastModified);
        } catch (IOException e) {
            String msg = String.format("Error accessing metadata for file %s", filePath);
            log.error(msg);
            throw new RuntimeException(msg, e);
        }
        return result;
    }

}
