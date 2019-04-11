package de.codereview.springboot.fileserver.service.plugin;

import de.codereview.fileserver.api.v1.Converter;
import de.codereview.fileserver.api.v1.ConverterResult;
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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ConverterService
{
    private static final Logger log = LoggerFactory.getLogger(ConverterService.class);

    private Map<String, Converter> sourceTargetMap = new HashMap<>();

    @Autowired
    public ConverterService(AsciidocHtml asciidoc,
                            Collection<Converter> converters, PluginProperties pluginProps)
    {
        registerConverter(asciidoc);
        for (Converter converter : converters) {
            if (!(converter instanceof AsciidocHtml)) {
                registerConverter(converter);
            }
        }
        ArrayList<ConverterConfig> addConverters = new ArrayList<>(pluginProps.getConverters());
        for (ConverterConfig config : addConverters) {
            String className = config.getClassname();
            Map<String,String> props = config.getConfig();
            // not like this: http://stackoverflow.com/a/36228195/650411
//            Class clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            try {
                Class clazz = Class.forName(className);
                Converter converter;
                try {
                    log.debug("Using converter config {}", props);
                    Constructor c = clazz.getConstructor(Map.class);
                    converter = (Converter) c.newInstance(props);
                } catch (NoSuchMethodException | InvocationTargetException e) {
                    log.trace("Instantiating without config.");
                    // no config, ok
                    converter = (Converter) clazz.newInstance();
                }
                registerConverter(converter);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                log.error("Error instantiating '{}' as Converter", className, e);
            }
        }
    }

    private void registerConverter(Converter converter)
    {
        String source = converter.getSource();
        String target = converter.getTarget();
        if (isConversionAvailable(source, target)) {
            log.warn("Overriding converter {} by...", getConverter(source, target).getClass().getName());
        }
        log.info("Registering converter to {} from {}: {}",
            target, source, converter.getClass().getName());
        sourceTargetMap.put(source + ";" + target, converter);
    }

    public boolean isConversionAvailable(String source, String target)
    {
        return getConverter(source, target) != null;
    }

    private Converter getConverter(String source, String target)
    {
        String key = source + ";" + target;
        return sourceTargetMap.get(key.intern());
    }

    public ConverterResult convert(byte[] data, String source, String target, String filename, String sourceEncoding, String sourceLanguage)
    {
        Converter converter = getConverter(source, target);
        if (converter == null) {
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
     *
     * @param target mimetype
     * @return file extension according to Apache Tika
     */
    public String getTargetExtension(String target)
    {
        String extension;
        try {
            TikaConfig config = TikaConfig.getDefaultConfig();
            MimeType mimeType = config.getMimeRepository().forName(MediaType.parse(target).toString());
            extension = mimeType.getExtension();
        } catch (MimeTypeException e) {
            log.error("Error finding extension for mimetype from tika");
            extension = "";
        }
        return extension;
    }
}
