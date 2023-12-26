package de.codereview.springboot.fileserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.codereview.fileserver.api.v1.ConverterResult;
import de.codereview.springboot.fileserver.service.FileResult;
import de.codereview.springboot.fileserver.service.FileService;
import de.codereview.springboot.fileserver.service.HtmlService;
import de.codereview.springboot.fileserver.service.plugin.ConverterService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/")
public class FileController
{
    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;
    @Autowired
    private ConverterService converterService;
    @Autowired
    private HtmlService htmlService;

    @Autowired
    Environment environment;

    @Value("${server.port}")
    private int serverPort;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/static/**", method = RequestMethod.GET)
    public void downloadFile(HttpServletRequest request, HttpServletResponse response)
    {
        String path = request.getRequestURI();
        try {
            byte[] fileContent = IOUtils.toByteArray(
                FileController.class.getResourceAsStream(path));
            response.getOutputStream().write(fileContent);
        } catch (IOException e) {
            log.warn("Error loading resource from static path '{}'", path, e);
        }
    }

    @Autowired
    public FileController(FileService fileService, ConverterService converterService,
                          HtmlService htmlService)
    {
        this.fileService = fileService;
        this.converterService = converterService;
        this.htmlService = htmlService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/html")
    @ResponseBody
    public Object index()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><head><title>");
        builder.append("springboot-fileserver");
        builder.append("</title></head><body><h2>");
        builder.append("Your boxes");
        builder.append("</h2><ul>");
        fileService.getBoxList().forEach(box ->
            builder.append(String.format("<li><a href=\"http://localhost:%d/%s\">%s</a></li>", serverPort, box, box)));
        builder.append("</ul></body></html>");
        return builder.toString();
    }

//    @RequestMapping(value = "/favicon.ico", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    @RequestMapping(value = "/favicon.ico", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> favicon(@RequestHeader("Accept") String accept) throws IOException
    {
        InputStream in = getClass().getResourceAsStream("/android-chrome-512x512.png");
        assert in != null;
        return ResponseEntity.ok(IOUtils.toByteArray(in));
    }

    @RequestMapping(value = "/{box}/**", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<?> file(@PathVariable String box, @RequestHeader Map<String, String> header,
                       HttpServletRequest request,
                       @RequestParam(name = "recursive", required = false) String recursive)
    {
        String relpath = getRelativePathInBox(box, request);
        log.debug("file-accessing '{}' from box '{}'", relpath, box);

        try {
            FileResult fileResult = fileService.getFile(box, relpath, recursive != null);
            if (fileResult.isDirectory()) {
                // TODO: either directoryAsJson or directoryAsHtml
                return directoryAsHtml(box, header, request, recursive);
            } else {
                header.keySet().forEach(key -> log.trace("{} : {}", key, header.get(key)));

                NegotiationResult negResult = negotiateResponseFormat(fileResult, header);

                HttpHeaders headers = getResponseHeaders(request, fileResult, negResult);

                return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .body(negResult.content);
            }
        } catch (IOException e) {
            String msg = "Error accessing box storage";
            log.error(msg);
            throw new IllegalArgumentException(msg, e);
        }
    }

    public ResponseEntity<?> directoryAsHtml(@PathVariable String box, @RequestHeader Map<String, String> header,
                                  HttpServletRequest request,
                                  @RequestParam(name = "recursive", required = false) String recursive)
    {
        String relpath = getRelativePathInBox(box, request);
        log.debug("dir-accessing '{}' from box '{}'", relpath, box);
        try {
            FileResult fileResult = fileService.getFile(box, relpath, recursive != null);
            if (fileResult.isDirectory()) {
                Path boxPath = fileService.getBoxPath(box);
                Path filePath;
                if (fileResult.getParentPath() == null) { // box root
                    filePath = boxPath;
                } else {
                    Path parentPath = boxPath.resolve(fileResult.getParentPath());
                    filePath = parentPath.resolve(fileResult.getFilename());
                }
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_TYPE, "text/html;charset=UTF-8");
                return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .body(htmlService.listDirectory(filePath, box, boxPath, request.getContextPath()));
            } else {
                return file(box, header, request, recursive);
            }
        } catch (IOException e) {
            String msg = "Error accessing box storage";
            log.error(msg);
            throw new IllegalArgumentException(msg, e);
        }
    }

    @ResponseBody
    public ResponseEntity<?> directoryAsJson(@PathVariable String box, @RequestHeader Map<String, String> header,
                                  HttpServletRequest request,
                                  @RequestParam(name = "recursive", required = false) String recursive)
    {
        String relpath = getRelativePathInBox(box, request);
        log.debug("accessing '{}' from box '{}'", relpath, box);
        try {
            FileResult fileResult = fileService.getFile(box, relpath, recursive != null);
            if (fileResult.isDirectory()) {
                String json = new ObjectMapper().writeValueAsString(fileResult);
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
                return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .body(json);
            } else {
                return file(box, header, request, recursive);
            }
        } catch (IOException e) {
            String msg = "Error accessing box storage";
            log.error(msg);
            throw new IllegalArgumentException(msg, e);
        }
    }

    private static HttpHeaders getResponseHeaders(HttpServletRequest request,
                                           FileResult fileResult, NegotiationResult negResult)
    {
        HttpHeaders result = new HttpHeaders();
        String url = request.getRequestURL().toString();
        String orgMimeType = fileResult.getHeader().get(HttpHeaders.CONTENT_TYPE);
        String newMimeType = negResult.mimetype;
        if (!Objects.equals(newMimeType, orgMimeType)) {
            result.set(HttpHeaders.LINK,
                "<" + url + ">; rel=\"alternate\";type=\"" + orgMimeType + "\"");
        }
        if (fileResult.isTextual()) {
            result.set(HttpHeaders.CONTENT_TYPE, newMimeType
                + ";charset=" + negResult.encoding);
            result.set(HttpHeaders.CONTENT_DISPOSITION,
                "filename=" + negResult.filename);
        } else { // binary content
            result.set(HttpHeaders.CONTENT_TYPE, orgMimeType);
            result.set(HttpHeaders.CONTENT_DISPOSITION,
                "filename=" + negResult.filename); // let the browser decide (show|download attachment)
//                        "attachment;filename=" + negResult.filename);
        }
        if (negResult.language != null) {
            result.set(HttpHeaders.CONTENT_LANGUAGE, negResult.language);
        }
        result.set(HttpHeaders.CONTENT_LENGTH, "" + negResult.content.length);
        return result;
    }

    private static String getRelativePathInBox(@PathVariable String box, HttpServletRequest request)
    {
        String path = request.getRequestURI(); // "/fs/box/..."
        // alt: request.getServletPath();
        // alt: request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
//        String baseUrl = url.substring(0, url.indexOf(uri)); // "http://localhost:3"
//        ServletUriComponentsBuilder.fromPath("/demo/").build().toUriString();

        path = path.substring(box.length() + 1); // box root
        if (path.startsWith("/")) {
            path = path.substring(1); // subdir
        }

        return path;
    }

    private NegotiationResult negotiateResponseFormat(FileResult fileResult, Map<String, String> header)
        throws IOException
    {
        String filename = fileResult.getFilename();
        String mimetype = fileResult.getHeader().get(HttpHeaders.CONTENT_TYPE);
        NegotiationResult result = new NegotiationResult(filename, mimetype,
            fileService.readFile(fileResult));
        result.encoding = fileResult.getEncoding();
        String acceptHeader = header.getOrDefault("Accept", header.get("accept"));
        List<MediaType> acceptedTypes = MediaType.parseMediaTypes(acceptHeader);
        // negotiate mimetype
        Charset acceptedCharset = null;
        for (MediaType mediaType : acceptedTypes) {
            String targetType = mediaType.getType() + "/" + mediaType.getSubtype();
            acceptedCharset = mediaType.getCharset();
            if ("*/*".equals(targetType)) {
                break;
            }
            if (converterService.isConversionAvailable(mimetype, targetType)) {
                log.debug("conversion from {} to {}", mimetype, targetType);
                ConverterResult convResult = converterService.convert(result.content, mimetype, targetType,
                    filename, fileResult.getParentPath(), fileResult.getEncoding(), fileResult.getLanguage());
                result.filename = filename + converterService.getTargetExtension(targetType);
                result.mimetype = targetType;
                result.content = convResult.getContent();
                result.encoding = convResult.getEncoding();

                // TODO: maybe redirect to url with extended extension?
//                        response.sendRedirect("some-url"); // maybe forward, not rediret
                break;
            }
        }
        // negotiate language, TODO: on-the-fly translation...
        String langHeader = header.get(HttpHeaders.CONTENT_LANGUAGE);
        if (fileResult.getLanguage() != null) {
            result.language = fileResult.getLanguage();
        }
        // adapt encoding
        if (acceptedCharset != null) {
            try {
                log.info("converting from {} to {}", result.encoding, acceptedCharset.name());
                result.content = new String(result.content, result.encoding).getBytes(acceptedCharset);
                result.encoding = acceptedCharset.name();
            } catch (Exception e) {
                log.error("Error converting to charset from Accept Header: '{}', fallback to file charset '{}'",
                    acceptedCharset.name(), result.encoding, e);
            }
        }
        return result;
    }

    private static class NegotiationResult
    {
        String filename;
        String mimetype;
        byte[] content;

        String language;
        String encoding;

        NegotiationResult(String filename, String mimetype, byte[] content)
        {
            this.filename = filename;
            this.mimetype = mimetype;
            this.content = content;
        }
    }
}
