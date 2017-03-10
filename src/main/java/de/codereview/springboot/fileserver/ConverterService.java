package de.codereview.springboot.fileserver;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ConverterService
{
    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    private Map<String, Converter> converters = new HashMap<>();

    public ConverterService() {
        Converter markdown = new MarkdownHtmlConverter();
        converters.put(markdown.getSource() + ";" + markdown.getTarget(), markdown);
        // TODO: externalize configuration
    }

    public boolean isConversionAvailable(String source, String target) {
        return getConverter(source, target) != null;
    }

    private Converter getConverter(String source, String target) {
        String key = source + ";" + target;
        return converters.get(key.intern());
    }

    public byte[] convert(byte[] data, String source, String target, String title) {
        Converter converter = getConverter(source, target);
        if (converter==null) {
            String message = String.format("no converter from %s to %s registered.", source, target);
            log.error(message);
            throw new RuntimeException(message);
        }
        return converter.convert(data, title);
    }

    public String getTargetExtension(String target) {
        String extension;
        try
        {
            TikaConfig config = TikaConfig.getDefaultConfig();
            MimeType mimeType = config.getMimeRepository().forName(MediaType.parse(target).toString());
            extension = mimeType.getExtension();
        } catch (MimeTypeException e)
        {
            log.error("Error finding extension for mimetype from tika");
            extension = "";
        }
        return extension;
    }
}
