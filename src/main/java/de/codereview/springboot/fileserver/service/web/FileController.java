package de.codereview.springboot.fileserver.service.web;

import de.codereview.fileserver.api.v1.ConverterResult;
import de.codereview.springboot.fileserver.service.FileResult;
import de.codereview.springboot.fileserver.service.FileService;
import de.codereview.springboot.fileserver.service.plugin.ConverterService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fs")
public class FileController
{
    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    private final FileService fileService;
    private final ConverterService converterService;

    private static final int PREFIX_PATH_LENGTH = "/fs/".length();

    @Autowired
    public FileController(FileService fileService, ConverterService converterService)
    {
        this.fileService = fileService;
        this.converterService = converterService;
    }

    @RequestMapping(value = "/{box}/**", method = {RequestMethod.GET})
    public Object file(@PathVariable String box,
                       HttpServletRequest request, @RequestHeader Map<String, String> header,
                       HttpServletResponse response)
    {
        String uri = request.getRequestURI(); // "/fs/box/..."
        // alt: request.getServletPath();
        // alt: request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String url = request.getRequestURL().toString(); // "http://localhost:8001/fs/box/..."
//        String baseUrl = url.substring(0, url.indexOf(uri)); // "http://localhost:8001"
//        ServletUriComponentsBuilder.fromPath("/demo/").build().toUriString();
        String relpath = uri.substring(PREFIX_PATH_LENGTH + box.length() + 1);

        try {
            FileResult fileResult = fileService.getFile(box, relpath);
            if (fileResult.isDirectory()) {
                throw new RuntimeException("accessing directories not implemented in service api");
            } else {
                header.keySet().forEach(elem -> log.trace(elem + " : " + header.get(elem)));

                String orgMimeType = fileResult.getMimeType();
                NegotiationResult negResult = negotiateResponseFormat(fileResult, header);
                String newMimeType = negResult.mimetype;
                if (!newMimeType.equals(orgMimeType)) {
                    response.setHeader(HttpHeaders.LINK,
                        "<" + url + ">; rel=\"alternate\";type=\"" + orgMimeType + "\"");
                }
                if (fileResult.isTextual()) {
                    response.setHeader(HttpHeaders.CONTENT_TYPE, newMimeType
                        + ";charset=" + negResult.encoding);
                    response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                        "filename=" + negResult.filename);
                } else { // binary content
                    response.setHeader(HttpHeaders.CONTENT_TYPE, orgMimeType);
                    response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                        "filename=" + negResult.filename); // let the browser decide (show|download attachment)
//                        "attachment;filename=" + negResult.filename);
                }
                if (negResult.language != null) {
                    response.setHeader(HttpHeaders.CONTENT_LANGUAGE, negResult.language);
                }
                response.setHeader(HttpHeaders.CONTENT_LENGTH, "" + negResult.content.length);

                Map<String, String> metadata = fileResult.getMetadata();
                metadata.forEach(response::setHeader);
                return negResult.content;
            }
        } catch (IOException e) {
            String msg = "Error accessing box storage";
            log.error(msg);
            throw new IllegalArgumentException(msg, e);
        }
    }

    private NegotiationResult negotiateResponseFormat(FileResult fileResult, Map<String, String> header)
    {
        String filename = fileResult.getFilename();
        String mimetype = fileResult.getMimeType();
        NegotiationResult result = new NegotiationResult(filename, mimetype, fileResult.getContent());
        result.encoding = fileResult.getEncoding();
        String acceptHeader = header.get(HttpHeaders.ACCEPT);
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
                ConverterResult convResult = converterService.convert(fileResult.getContent(), mimetype, targetType,
                    filename, fileResult.getEncoding(), fileResult.getLanguage());
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

    private class NegotiationResult
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
