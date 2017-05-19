package de.codereview.springboot.fileserver.service.plugin.converter;

import de.codereview.springboot.fileserver.service.plugin.ConverterResult;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MarkdownHtmlTest
{
    MarkdownHtml service = new MarkdownHtml();

    @Test
    public void utf8() throws Exception
    {
        ConverterResult result = service.convert("# öäüßÖÄÜ".getBytes(), "UTF-8", "de", "DUMMY");
        assertThat(new String(result.getContent(), result.getEncoding())).contains("<title>öäüßÖÄÜ</title>");
        assertThat(new String(result.getContent(), result.getEncoding())).contains("<h1>öäüßÖÄÜ</h1>");
        assertThat(result.getTitle()).isEqualTo("öäüßÖÄÜ");
    }

    @Test
    public void iso_8859_1() throws Exception
    {
        final String ENCODING = "iso-8859-1";
        byte[] source = "# öäüßÖÄÜ".getBytes(ENCODING);
        ConverterResult result = service.convert(source, ENCODING, "de", "DUMMY");
        assertThat(new String(result.getContent(), result.getEncoding())).contains("<title>öäüßÖÄÜ</title>");
        assertThat(new String(result.getContent(), result.getEncoding())).contains("<h1>öäüßÖÄÜ</h1>");
        assertThat(result.getTitle()).isEqualTo("öäüßÖÄÜ");
    }

}
