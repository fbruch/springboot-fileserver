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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        if (root.getEncoding() != null) {
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
        // TODO: configurable exclude filters
        Stream<Path> stream = Files.list(dirPath).filter(path -> ! path.toFile().getName().startsWith("."));
        return stream;
    }

    public FileResult getFile(String box, String path, boolean recursive) throws IOException
    {
        return getFile(box, path, recursive, false);
    }

    private FileResult getFile(String box, String path, boolean recursive, boolean stop) throws IOException
    {
        Path boxPath = getBoxPath(box.intern());
        if (boxPath == null) {
            throw new RuntimeException(String.format("unknown box: '%s'", box));
        }
        Path absPath = boxPath.resolve(path);
        String filename;
        if ("".equals(path)) {
            filename = "";
        } else {
            filename = absPath.getFileName().toString();
        }
        FileResult result;
        if (! Files.isDirectory(absPath)) {
            result = new FileResult(box, path, filename, false);
            result.setHeader(getFileMetadata(absPath));
            String mimeType = fileTypeService.detectMimeType(absPath);
            result.getHeader().put(HttpHeaders.CONTENT_TYPE, mimeType);
            boolean textual = fileTypeService.isTextual(mimeType);
            result.setTextual(textual);
            result.setLanguage(getBoxLanguage(box));
            if (textual) {
                result.setEncoding(getBoxEncoding(box));
            }
        } else if (! stop) {
            List<FileResult> files = new ArrayList<>();
            getFileList(absPath).forEach(fpath -> {
                try {
                    Path relPath = boxPath.relativize(fpath);
                    FileResult fresult = getFile(box, relPath.toString(), recursive, !recursive);
                    fresult.setBox(null);
                    fresult.setParentPath(null);
                    files.add(fresult);
                } catch (IOException e) {
                    log.warn(e.getMessage(), e);
                    // TODO
                }
            });
            result = new DirResult(box, path, filename, files);
            // TODO: result.setHeader(getDirMetadata(absPath)); // from metadata file...
        } else {
            result = new FileResult(box, path, filename, true);
            // TODO: result.setHeader(getDirMetadata(absPath)); // from metadata file...
        }
        if (".".equals(filename) || "".equals(filename)) {
            result.setParentPath(null);
        } else {
            String parentPath = boxPath.relativize(absPath.getParent()).toString();
            if ("..".equals(parentPath)) {
                parentPath = null;
            }
            result.setParentPath(parentPath);
        }
        return result;
    }

    public byte[] readFile(FileResult fileResult) throws IOException
    {
        Path boxPath = getBoxPath(fileResult.getBox());
        Path filePath;
        if ("".equals(fileResult.getParentPath())) {
            filePath = boxPath.resolve(fileResult.getFilename());
        } else {
            filePath = boxPath.resolve(fileResult.getParentPath() + "/" + fileResult.getFilename());
        }
        return readFile(filePath);
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
            result.put(HttpHeaders.CONTENT_LENGTH, "" + Files.size(filePath));
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
    private String getBoxEncoding(String box)
    {
        String encoding = boxes.get(box).getEncoding();
        if (encoding == null) {
            encoding = Charset.defaultCharset().name();
//        encoding = System.getProperty("file.encoding");
        }
        return encoding;
    }

    /**
     * @param box not null
     * @return null, if no language is configured for the given box
     */
    private String getBoxLanguage(String box)
    {
        String lang = boxes.get(box).getLanguage();
//        if (lang==null) {
//            lang = Locale.getDefault().getLanguage();
//            // alt: System.getProperty("user.language");
//        }
        return lang;
    }

}
