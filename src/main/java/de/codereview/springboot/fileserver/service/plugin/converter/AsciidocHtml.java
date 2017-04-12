package de.codereview.springboot.fileserver.service.plugin.converter;

import de.codereview.springboot.fileserver.service.plugin.Converter;
import de.codereview.springboot.fileserver.service.plugin.ConverterResult;
import de.codereview.springboot.fileserver.service.plugin.PluginProperties;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.asciidoctor.ast.StructuredDocument;
import org.asciidoctor.ast.Title;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

@Service
public class AsciidocHtml implements Converter
{
    private static final Logger log = LoggerFactory.getLogger(AsciidocHtml.class);

    private Asciidoctor asciidoctor;

    private Map<String, String> properties;

    private Map<String, Object> options;

//    public AsciidocHtml(@Value("${fileserver.plugin.asciidoctor.images-dir}") String imageDir)
    @Autowired
    public AsciidocHtml(PluginProperties pluginProps)
    {
        asciidoctor = Asciidoctor.Factory.create();

        properties = new HashMap<>(pluginProps.getAsciidoctor());

        String imageDir = properties.get("images-dir");

        if (imageDir==null) imageDir=".";

        Map<String, Object> attributes = AttributesBuilder.attributes()
            .backend("html") // "docbook"
            .imagesDir(imageDir)
            .asMap();

        options = OptionsBuilder.options()
            .safe(SafeMode.SAFE)
            .headerFooter(true)
            .attributes(attributes)
            .asMap();
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
    public ConverterResult convert(byte[] source, String filename)
    {
        String text = new String(source); // TODO: charset?
        String title = giveTitle(source);
        if (title==null) title = filename;
        byte[] body = asciidoctor.convert(text, options).getBytes(); // TODO: charset?
        return new ConverterResult(body, title);
    }

    private String giveTitle(byte[] source)
    {
        String title = null;
        try (Reader targetReader = new InputStreamReader(new ByteArrayInputStream(source))) {
            StructuredDocument doc = asciidoctor.readDocumentStructure(targetReader, new HashMap<>());
            targetReader.close();
            Title docTitle = doc.getHeader().getDocumentTitle();
            if (docTitle != null) {
                title = docTitle.getMain();
            } else {
                title = doc.getParts().get(0).getTitle();
            }
        } catch (IOException e) {
            log.error("Error parsing for title", e);
        }
        return title;
    }
}
