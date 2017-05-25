package de.codereview.fileserver.plugin.javahtml;

import de.codereview.springboot.fileserver.service.plugin.Converter;
import de.codereview.springboot.fileserver.service.plugin.ConverterResult;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import static j2html.TagCreator.body;
import static j2html.TagCreator.header;
import static j2html.TagCreator.html;
import static j2html.TagCreator.link;
import static j2html.TagCreator.meta;
import static j2html.TagCreator.script;
import static j2html.TagCreator.tag;
import static j2html.TagCreator.title;

/**
 * @link http://prismjs.com/
 */
@Service
public class JavaHtml implements Converter
{
    @Override
    public String getSource()
    {
        return "text/x-java-source";
//        return "application/java";
    }

    @Override
    public String getTarget()
    {
        return "text/html";
    }

    @Override
    public ConverterResult convert(byte[] source, String sourceEncoding,
                                   String sourceLanguage, String filename)
        throws UnsupportedEncodingException
    {
        String html = html()
            .with(header().with(
                meta().attr("charset").withValue("utf-8")).with(
                title(filename)).with(
                link().withHref("/javahtml/prism.css")
                    .withRel("stylesheet").withType(("text/css"))
                    .attr("media").withValue("screen")))
            .with(body().with(
                tag("pre").with(
                    tag("code").withClass("language-java")
                        .withText(new String(source, sourceEncoding)))).with(
                script().withSrc("/javahtml/prism.js")
            )).render();

        ConverterResult result = new ConverterResult(
            html.getBytes(Charset.forName("UTF-8")), filename, sourceEncoding);

        return result;
    }
}
