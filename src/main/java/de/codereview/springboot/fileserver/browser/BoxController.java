package de.codereview.springboot.fileserver.browser;

import de.codereview.springboot.fileserver.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/")
public class BoxController
{
    private static final Logger log = LoggerFactory.getLogger(BoxController.class);

    private static final int PREFIX_PATH_LENGTH = "/fb/".length();

    private final FileService fileService;
    private final HtmlService htmlService;

    @Autowired
    public BoxController(FileService fileService, HtmlService htmlService)
    {
        this.fileService = fileService;
        this.htmlService = htmlService;
    }

    @RequestMapping(value = "/fb", method = RequestMethod.GET)
    public Object listBoxesWithRootFiles(HttpServletRequest request)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><head><title>");
        builder.append("Boxes");
        builder.append("</title></head><body><h1>");
        builder.append("Box list");
        builder.append("</h1>");
        for (String box : fileService.getBoxList()) {
            builder.append("<h2>");
            builder.append(box);
            builder.append("</h2>");
            try {
                Path boxPath = fileService.getBoxPath(box);
                htmlService.listDirectoryContentAsLinks(
                    builder, box, boxPath, boxPath, request.getContextPath());
            } catch (IOException e) {
                String msg = "Error parsing box directory";
                log.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        }
        builder.append("</body></html>");
        return builder.toString();
    }

    @RequestMapping(value = "/fb/{box}/**", method = RequestMethod.GET)
    public Object file(@PathVariable String box,
                       HttpServletRequest request)
    {
        String path = (String) request.getAttribute(
            HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        // alternative: request.getServletPath();

        path = path.substring(PREFIX_PATH_LENGTH + box.length() + 1);
        Path filePath = fileService.getFilePath(box, path);
        if (!Files.isDirectory(filePath)) {
            throw new RuntimeException("accessing files not implemented in browser api");
        } else {
            Path boxPath = fileService.getBoxPath(box);
            return htmlService.listDireectory(filePath, box, boxPath, request.getContextPath());
        }
    }

}
