package de.codereview.springboot.fileserver.service.web;

import de.codereview.springboot.fileserver.service.FileService;
import de.codereview.springboot.fileserver.service.FileTypeService;
import de.codereview.springboot.fileserver.service.plugin.ConverterService;
import de.codereview.springboot.fileserver.service.plugin.ConverterResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fs")
public class FileController
{
    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    private final FileService fileService;
    private final FileTypeService fileTypeService;
    private final ConverterService converterService;

    private static final int PREFIX_PATH_LENGTH = "/fs/".length();

    @Autowired
    public FileController(FileService fileService, FileTypeService fileTypeService, ConverterService converterService)
    {
        this.fileService = fileService;
        this.fileTypeService = fileTypeService;
        this.converterService = converterService;
    }

    @RequestMapping(value = "/{box}/**", method = RequestMethod.GET)
    public Object file(@PathVariable String box,
                       HttpServletRequest request,
                       @RequestHeader Map<String, String> header,
                       HttpServletResponse response)
    {
        String path = (String) request.getAttribute(
            HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        // alternative: request.getServletPath();

        path = path.substring(PREFIX_PATH_LENGTH + box.length() + 1);
        Path filePath = fileService.getFilePath(box, path);
        try {
            if (Files.isDirectory(filePath)) {
                throw new RuntimeException("accessing directories not implemented in service api");
            } else {
                String mimetype = fileTypeService.detectMimeType(filePath);
                header.keySet().forEach(elem -> log.trace(elem + " : " + header.get(elem)));
                String acceptHeader = header.get("accept");
                log.debug("accept header is '{}'", acceptHeader);
                byte[] bytes = fileService.readFile(filePath);
                String filename = filePath.getFileName().toString();
                List<MediaType> acceptedTypes = MediaType.parseMediaTypes(acceptHeader);
                ConverterResult result = new ConverterResult(bytes, filename);
                for (MediaType mediaType : acceptedTypes) {
                    String target = mediaType.toString();
                    if (converterService.isConversionAvailable(mimetype, target)) {
                        log.debug("conversion from {} to {}", mimetype, target);
                        // TODO better title than filename?
                        result = converterService.convert(bytes, mimetype, target, filename);
                        mimetype = target;
                        filename = filename + converterService.getTargetExtension(target);
                        // TODO: redirect to url with extended extension?
//                        response.sendRedirect("some-url"); // maybe forward, not rediret
                        break;
                    }
                }
                response.setHeader(HttpHeaders.CONTENT_TYPE, mimetype);
                response.setHeader(HttpHeaders.CONTENT_LENGTH, "" + result.getContent().length);
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "filename=" + filename);
                // TODO
//                response.setHeader(HttpHeaders.CONTENT_ENCODING, "filename=" + filename);
                // TODO
//                response.setHeader(HttpHeaders.CONTENT_LANGUAGE, "filename=" + filename);

                Map<String, String> metadata = fileService.getFileMetadata(filePath);
                metadata.forEach(response::setHeader);
                return result.getContent();
            }
        } catch (IOException e) {
            String msg = "Error accessing box storage";
            log.error(msg);
            throw new IllegalArgumentException(msg, e);
        }
    }
}
