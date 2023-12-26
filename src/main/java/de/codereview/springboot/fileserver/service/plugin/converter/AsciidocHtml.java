package de.codereview.springboot.fileserver.service.plugin.converter;

import de.codereview.fileserver.api.v1.Converter;
import de.codereview.fileserver.api.v1.ConverterResult;
import de.codereview.springboot.fileserver.service.plugin.PluginProperties;
import org.asciidoctor.*;
import org.asciidoctor.ast.DocumentHeader;
import org.asciidoctor.ast.Title;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Support included documents, see https://docs.asciidoctor.org/asciidoctorj/latest/locating-files/
 */
@Service
public class AsciidocHtml implements Converter
{
    private static final Logger log = LoggerFactory.getLogger(AsciidocHtml.class);

    private Asciidoctor asciidoctor;

    private Map<String, String> properties;

//    public AsciidocHtml(@Value("${fileserver.plugin.asciidoctor.images-dir}") String imageDir)
    @Autowired
    public AsciidocHtml(PluginProperties pluginProps)
    {
        asciidoctor = Asciidoctor.Factory.create();
        properties = new HashMap<>(pluginProps.getAsciidoctor());
    }

    @Override
    public String getSource()
    {
        return "text/asciidoc";
    }

    @Override
    public String getTarget()
    {
        return "text/html";
    }

    @Override
    public ConverterResult convert(byte[] source, String sourceEncoding, String sourceLanguage, String filename) throws UnsupportedEncodingException
    {
        String text = new String(source, sourceEncoding);
        if (!"UTF-8".equalsIgnoreCase(sourceEncoding)) {
            log.warn("Asciidoctor does not support encodings other than UTF-8, converting '{}'...", sourceEncoding);
            // https://github.com/asciidoctor/asciidoctor.org/issues/160
        }
        String title = giveTitle(source, sourceEncoding);
        if (title==null) title = filename;
        Options options = getOptions(sourceLanguage);
        byte[] body = asciidoctor.convert(text, options).getBytes("UTF-8");
        return new ConverterResult(body, title, "UTF-8");
    }

    private Options getOptions(String sourceLanguage)
    {
        String imageDir = properties.get("images-dir");

        if (imageDir==null) imageDir=".";

        Attributes attributes = Attributes.builder()
            .backend("html") // or "docbook"
            .imagesDir(imageDir)
            .build();

        if (sourceLanguage!=null) {
            attributes.setAttribute("lang", sourceLanguage);
        }

        return Options.builder()
            .safe(SafeMode.SAFE)
            .standalone(true) // header and footer
            .attributes(attributes)
            .build();
    }

    private String giveTitle(byte[] source, String encoding)
    {
        String title = null;
        try (Reader targetReader = new InputStreamReader(
            new ByteArrayInputStream(source), encoding))
        {
            DocumentHeader doc = asciidoctor.readDocumentHeader(targetReader);
            targetReader.close();
            Title docTitle = doc.getDocumentTitle();
            if (docTitle != null) {
                title = docTitle.getMain();
            }
        } catch (IOException e) {
            log.error("Error parsing for title", e);
        }
        return title;
    }
}
