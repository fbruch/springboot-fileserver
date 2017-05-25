package de.codereview.springboot.fileserver.service.plugin;

import de.codereview.springboot.fileserver.service.plugin.converter.AsciidocHtml;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ConverterService
{
    private static final Logger log = LoggerFactory.getLogger(ConverterService.class);

//    @Autowired
//    Collection<Converter> converters;

    private Map<String, Converter> sourceTargetMap = new HashMap<>();

    @Autowired
    public ConverterService(AsciidocHtml asciidoc, Collection<Converter> converters) {
        registerConverter(asciidoc);
        for (Converter converter : converters) {
            if (!(converter instanceof AsciidocHtml)) {
                registerConverter(converter);
            }
        }
/*
        String className = null;
        try {
            className = "de.codereview.fileserver.plugin.javahtml.JavaHtml";
            // not like this: http://stackoverflow.com/a/36228195/650411
//            Class clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            Class clazz = Class.forName(className);
            Converter converter = (Converter) clazz.newInstance();
            // TODO: prevent duplicate registering...
            registerConverter(converter);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            log.error("Error instantiating '{}' as Converter", className, e);
        }
*/
    }

    private void registerConverter(Converter converter)
    {
        log.info("Registering converter {} from {} to {}", converter.getClass().getName(),
            converter.getSource(), converter.getTarget());
        sourceTargetMap.put(converter.getSource() + ";" + converter.getTarget(), converter);
    }

    public boolean isConversionAvailable(String source, String target) {
        return getConverter(source, target) != null;
    }

    private Converter getConverter(String source, String target) {
        String key = source + ";" + target;
        return sourceTargetMap.get(key.intern());
    }

    public ConverterResult convert(byte[] data, String source, String target, String filename, String sourceEncoding, String sourceLanguage) {
        Converter converter = getConverter(source, target);
        if (converter==null) {
            String message = String.format("no converter from %s to %s registered.", source, target);
            log.error(message);
            throw new RuntimeException(message);
        }
        try {
            return converter.convert(data, sourceEncoding, sourceLanguage, filename);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding", e);
        }
    }

    /**
     * give file extension for given mimetype
     * @param target mimetype
     * @return file extension according to Apache Tika
     */
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
