package de.codereview.springboot.fileserver.browser;

import de.codereview.springboot.fileserver.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
class HtmlService
{
    private FileService fileService;

    public HtmlService(@Autowired FileService fileService)
    {
        this.fileService = fileService;
    }

    String listDireectory(Path dir, String box, Path boxPath, String contextPath)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><head><title>");
        builder.append(boxPath.relativize(dir));
        builder.append("</title></head><body><h2>");
        builder.append(boxPath.relativize(dir));
        builder.append("</h2>");
        try {
            listDirectoryContentAsLinks(builder, box, dir, boxPath, contextPath);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing box directory", e);
        }
        builder.append("</body></html>");
        return builder.toString();
    }

    void listDirectoryContentAsLinks(
        StringBuilder builder, String box,
        Path dirPath, Path boxPath, String contextPath) throws IOException
    {
        fileService.listDir(dirPath).forEach(path ->
        {
            builder.append("<li><a href=\"");
            //				builder.append("http://localhost:8080/");
            Path relativeToBoxPath = boxPath.relativize(path);
            Path relativeToDirPath = dirPath.relativize(path);
            if (Files.isDirectory(path)) {
                builder.append(contextPath + "/fb/" + box + "/" + relativeToBoxPath + "/");
                builder.append("\">");
                builder.append(relativeToDirPath);
            } else {
                builder.append(contextPath + "/fs/" + box + "/" + relativeToBoxPath);
                builder.append("\">");
                builder.append(relativeToDirPath);
            }
            builder.append("</a></li>");
        });
    }

}
