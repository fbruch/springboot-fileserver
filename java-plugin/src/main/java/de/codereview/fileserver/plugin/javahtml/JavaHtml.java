package de.codereview.fileserver.plugin.javahtml;

import de.codereview.fileserver.api.v1.Converter;
import de.codereview.fileserver.api.v1.ConverterResult;
import j2html.tags.ContainerTag;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

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
public class JavaHtml implements Converter
{
    private final String config;
    private final boolean lineNumbers;

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

    public JavaHtml()
    {
        config = "prism";
//        config = "dark";
        lineNumbers = false;
    }

    public JavaHtml(Map<String,String> props)
    {
        config = props.get("theme");
        lineNumbers = Boolean.valueOf(props.get("line-numbers"));
    }

    @Override
    public ConverterResult convert(byte[] source, String sourceEncoding,
                                   String sourceLanguage, String filename)
        throws UnsupportedEncodingException
    {
        ContainerTag pre;
        if (lineNumbers) {
            pre = tag("pre").withClass("line-numbers");
        } else {
            pre = tag("pre");
        }
        String html = html()
            .with(header().with(
                meta().attr("charset").withValue("utf-8")).with(
                title(filename)).with(
                link().withHref("/" + config + "/prism.css")
                    .withRel("stylesheet").withType(("text/css"))
                    .attr("media").withValue("screen")))
            .with(body().with(
                pre.with(
                    tag("code").withClass("language-java")
                        .withText(new String(source, sourceEncoding)))).with(
                script().withSrc("/" + config + "/prism.js")
            )).render();

        ConverterResult result = new ConverterResult(
            html.getBytes(Charset.forName("UTF-8")), filename, sourceEncoding);

        return result;
    }
}
