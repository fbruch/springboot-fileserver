package de.codereview.springboot.fileserver;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkdownHtmlConverter implements Converter
{
    private static final Logger log = LoggerFactory.getLogger(MarkdownHtmlConverter.class);

    @Override
    public String getSource() {
        return "text/markdown";
    }

    @Override
    public String getTarget() {
        return "text/html";
    }

    @Override
    public byte[] convert(byte[] source, String title)
    {
        String text = new String(source); // TODO: charset?
        Parser parser = Parser.builder().build();
        Node document = parser.parse(text);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        StringBuilder builder = new StringBuilder(); // TODO: some template engine?
        builder.append("<html>");
        builder.append(String.format("<head><title>%s</title></head>", title));
        builder.append("<body>");
        byte[] body = renderer.render(document).getBytes(); // TODO: charset?
        builder.append(new String(body));
        builder.append("</body></html>");
        return builder.toString().getBytes();
    }
}
