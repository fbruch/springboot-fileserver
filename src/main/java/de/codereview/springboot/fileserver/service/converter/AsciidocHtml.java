package de.codereview.springboot.fileserver.service.converter;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.asciidoctor.ast.StructuredDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    private Map<String, Object> options;

    public AsciidocHtml(@Value("${springboot-fileserver.converter.asciidoc.images.dir}") String imageDir)
    {
        asciidoctor = Asciidoctor.Factory.create();

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
    public Result convert(byte[] source, String filename)
    {
        String text = new String(source); // TODO: charset?
        String title = giveTitle(source);
        if (title==null) title = filename;
        byte[] body = asciidoctor.convert(text, options).getBytes(); // TODO: charset?
        return new Result(body, title);
    }

    private String giveTitle(byte[] source)
    {
        String title = null;
        try (Reader targetReader = new InputStreamReader(new ByteArrayInputStream(source))) {
            StructuredDocument doc = asciidoctor.readDocumentStructure(targetReader, new HashMap<>());
            targetReader.close();
            title = doc.getHeader().getDocumentTitle().getMain();
        } catch (IOException e) {
            log.error("Error parsing for title", e);
        }
        return title;
    }
}
