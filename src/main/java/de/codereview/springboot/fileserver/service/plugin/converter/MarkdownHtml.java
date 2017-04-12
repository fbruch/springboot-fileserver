package de.codereview.springboot.fileserver.service.plugin.converter;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import de.codereview.springboot.fileserver.service.plugin.Converter;
import de.codereview.springboot.fileserver.service.plugin.ConverterResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public ConverterResult convert(byte[] source, String filename)
    {
        String text = new String(source); // TODO: charset?
        Parser parser = Parser.builder().build();
        Node document = parser.parse(text);
        Heading heading = (Heading) document.getFirstChildAny(Heading.class);
        String title = heading.getText().toString();
        if (title==null) title = filename;
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        StringBuilder builder = new StringBuilder(); // TODO: some template engine?
        builder.append("<html>");
        builder.append(String.format("<head><title>%s</title></head>", title));
        builder.append("<body>");
        byte[] body = renderer.render(document).getBytes(); // TODO: charset?
        builder.append(new String(body));
        builder.append("</body></html>");
        return new ConverterResult(builder.toString().getBytes(), title);
    }
}
