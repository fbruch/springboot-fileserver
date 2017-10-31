package de.codereview.springboot.fileserver.browser;

import de.codereview.springboot.fileserver.service.FileResult;
import de.codereview.springboot.fileserver.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;

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
    public Object file(@PathVariable String box, HttpServletRequest request,
                       @RequestParam(name="recursive", required=false) String recursive)
    {
        String path = (String) request.getAttribute(
            HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        // alternative: request.getServletPath();

        path = path.substring(PREFIX_PATH_LENGTH + box.length()); // box root
        if (path.startsWith("/")) path = path.substring(1); // subdir
        FileResult fileResult;
        try {
            fileResult = fileService.getFile(box, path, recursive!=null);
        } catch (IOException e) {
            String msg = "Error accessing file";
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
        if (!fileResult.isDirectory()) {
            // TODO: maybe only metadata as html?
            throw new RuntimeException("accessing files not implemented in browser api");
        } else {
            Path boxPath = fileService.getBoxPath(box);
            Path parentPath = boxPath.resolve(fileResult.getParentPath());
            Path filePath = parentPath.resolve(fileResult.getFilename());
            return htmlService.listDirectory(filePath, box, boxPath, request.getContextPath());
        }
    }

}
