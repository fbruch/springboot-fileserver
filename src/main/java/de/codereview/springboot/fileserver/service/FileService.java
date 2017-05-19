package de.codereview.springboot.fileserver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
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

/**
 * FileService uses static list of directory roots with given names ("boxes")
 * and metadata and provides access to the files list, content and meta data.
 */
@Service
public class FileService
{
    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    private final Map<String, Path> roots = new HashMap<>();
    private final Map<String, FileServiceConfig.Root> boxes = new HashMap<>();

    private FileTypeService fileTypeService;

    @Autowired
    public FileService(FileServiceConfig config, FileTypeService fileTypeService)
    {
        config.getRoots().forEach(root -> boxes.put(root.getName(), process(root)));
        this.fileTypeService = fileTypeService;
        config.getRoots().forEach(root ->
            roots.put(root.getName(), Paths.get(root.getPath())));
    }

    private FileServiceConfig.Root process(FileServiceConfig.Root root)
    {
        FileServiceConfig.Root clone = (FileServiceConfig.Root) root.clone();
        if (root.getEncoding()!=null) {
            // normalize encoding name
            root.setEncoding(Charset.forName(root.getEncoding()).name());
        }
        return clone;
    }

    public Path getBoxPath(String box)
    {
        return roots.get(box);
    }

    public Set<String> getBoxList()
    {
        return boxes.keySet();
    }

    public Stream<Path> getFileList(Path dirPath) throws IOException
    {
        Stream<Path> stream = Files.list(dirPath);
        return stream;
    }

    public FileResult getFile(String box, String path) throws IOException
    {
        Path boxPath = getBoxPath(box.intern());
        if (boxPath==null) {
            throw new RuntimeException("no such box");
        }
        Path filePath = boxPath.resolve(path);
        boolean isDirectory = Files.isDirectory(filePath);
        FileResult result = new FileResult(box, path, filePath.getFileName().toString(), isDirectory);
        result.setParentPath(boxPath.relativize(filePath.getParent()).toString());
        if (!isDirectory) {
            // TODO: limit file size to read in completely, config threshold
            // TODO: otherwise stream...
            byte[] bytes = readFile(filePath);
            result.setContent(bytes);
            result.setMetadata(getFileMetadata(filePath));
            String mimeType = fileTypeService.detectMimeType(filePath);
            result.setMimeType(mimeType);
            boolean textual = fileTypeService.isTextual(mimeType);
            result.setTextual(textual);
            result.setLanguage(getBoxLanguage(box));
            if (textual) {
                result.setEncoding(getBoxEncoding(box));
            }
        } else {
            result.setDirectory(true);
            // TODO: result.setMetadata(getDirMetadata(filePath));
        }
        return result;
    }

    byte[] readFile(Path filePath) throws IOException
    {
        return Files.readAllBytes(filePath);
    }

    Map<String, String> getFileMetadata(Path filePath)
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

    /**
     * @param box not null
     * @return default charset, if no encoding is configured for the given box
     */
    String getBoxEncoding(String box)
    {
        String encoding = boxes.get(box).getEncoding();
        if (encoding==null) {
            encoding = Charset.defaultCharset().name();
//        encoding = System.getProperty("file.encoding");
        }
        return encoding;
    }

    /**
     * @param box not null
     * @return null, if no language is configured for the given box
     */
    String getBoxLanguage(String box)
    {
        String lang = boxes.get(box).getLanguage();
//        if (lang==null) {
//            lang = Locale.getDefault().getLanguage();
//            // alt: System.getProperty("user.language");
//        }
        return lang;
    }
}
