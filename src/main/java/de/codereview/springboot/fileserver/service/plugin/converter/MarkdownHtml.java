package de.codereview.springboot.fileserver.service.plugin.converter;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import de.codereview.springboot.fileserver.service.plugin.Converter;
import de.codereview.springboot.fileserver.service.plugin.ConverterResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class MarkdownHtml implements Converter
{
    private static final Logger log = LoggerFactory.getLogger(MarkdownHtml.class);

    @Override
    public String getSource() {
        return "text/markdown";
    }

    @Override
    public String getTarget() {
        return "text/html";
    }

    @Override
    public ConverterResult convert(byte[] source, String sourceEncoding, String sourceLanguage, String filename) throws UnsupportedEncodingException
    {
        String text = new String(source, sourceEncoding);
        Parser parser = Parser.builder().build();
        Node document = parser.parse(text);
        Heading heading = (Heading) document.getFirstChildAny(Heading.class);
        String title = filename; // fallback
        if (heading != null && heading.getText()!=null) {
            title = heading.getText().toString();
        }
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>");
        if (sourceLanguage!=null) {
            builder.append(String.format("<html lang=\"%s\"><head>", sourceLanguage));
        } else {
            builder.append("<html><head>");
        }
        builder.append("<meta charset=\"UTF-8\">");
        builder.append("<meta name=\"generator\" content=\"flexmark-java\">");
        builder.append(String.format("<title>%s</title>", title));
        builder.append("</head><body>");
        byte[] body = renderer.render(document).getBytes("UTF-8");
        builder.append(new String(body));
        builder.append("</body></html>");
        return new ConverterResult(builder.toString().getBytes(), title, "UTF-8");
    }
}
