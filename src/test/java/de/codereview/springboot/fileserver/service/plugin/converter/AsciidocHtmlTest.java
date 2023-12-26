package de.codereview.springboot.fileserver.service.plugin.converter;

import de.codereview.fileserver.api.v1.ConverterResult;
import de.codereview.springboot.fileserver.service.plugin.PluginProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class AsciidocHtmlTest
{
    AsciidocHtml service;

    @BeforeEach
    public void setUp() {
        PluginProperties properties = new PluginProperties();
        properties.getAsciidoctor().put("images-dir", "images");
        service = new AsciidocHtml(properties);
    }

    @Test
    public void utf8() throws Exception
    {
        String LANGUAGE = "de";
        ConverterResult result = service.convert("# öäüßÖÄÜ".getBytes(), "UTF-8", LANGUAGE, "DUMMY");
        String content = new String(result.getContent(), result.getEncoding());
        assertThat(content).contains("<title>öäüßÖÄÜ</title>");
        assertThat(content).containsIgnoringCase("<html lang=\"" + LANGUAGE + "\">");
        assertThat(result.getTitle()).isEqualTo("öäüßÖÄÜ");
    }

    @Test
    public void iso_8859_1() throws Exception
    {
        final String ENCODING = "iso-8859-1";
        byte[] source = "# öäüßÖÄÜ".getBytes(ENCODING);
        String LANGUAGE = "de";
        ConverterResult result = service.convert(source, ENCODING, LANGUAGE, "DUMMY");
        String content = new String(result.getContent(), result.getEncoding());
        assertThat(content).contains("<title>öäüßÖÄÜ</title>");
        assertThat(content).containsIgnoringCase("<html lang=\"" + LANGUAGE + "\">");
        assertThat(result.getTitle()).isEqualTo("öäüßÖÄÜ");
    }

}
