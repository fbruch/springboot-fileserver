package de.codereview.springboot.fileserver.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class HtmlService
{
    private FileService fileService;

    public HtmlService(FileService fileService)
    {
        this.fileService = fileService;
    }

    public String listDirectory(Path dir, String box, Path boxPath, String contextPath)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><head><title>");
        builder.append(boxPath.relativize(dir));
        builder.append("</title></head><body><h2>");
        builder.append(box);
        builder.append("</h2><h3>");
        builder.append(boxPath.relativize(dir));
        builder.append("</h3>");
        try {
            fileService.getFileList(dir)
				.filter(path -> Files.isDirectory(path))
				.sorted()
				.forEach(path -> appendLink(builder, box, dir, boxPath, contextPath, path, "disc"));

            fileService.getFileList(dir)
				.filter(path -> ! Files.isDirectory(path))
				.sorted()
				.forEach(path -> appendLink(builder, box, dir, boxPath, contextPath, path, "circle"));
        } catch (IOException e) {
            throw new RuntimeException("Error parsing box directory", e);
        }
        builder.append("</body></html>");
        return builder.toString();
    }

    private void appendLink(StringBuilder builder, String box, Path dirPath, Path boxPath, String contextPath, Path path, String type)
    {
        builder.append("<li type=\"" + type + "\" ><a href=\"");
        Path relativeToBoxPath = boxPath.relativize(path);
        Path relativeToDirPath = dirPath.relativize(path);
        if (Files.isDirectory(path)) {
			builder.append(contextPath + "/" + box + "/" + relativeToBoxPath + "/");
			builder.append("\">");
			builder.append(relativeToDirPath);
			builder.append("/");
		} else {
			builder.append(contextPath + "/" + box + "/" + relativeToBoxPath);
			builder.append("\">");
			builder.append(relativeToDirPath);
		}
        builder.append("</a></li>");
    }

}
