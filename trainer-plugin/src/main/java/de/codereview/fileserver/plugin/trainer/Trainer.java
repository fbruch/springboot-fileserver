package de.codereview.fileserver.plugin.trainer;

import de.codereview.fileserver.api.v1.Converter;
import de.codereview.fileserver.api.v1.ConverterResult;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

public class Trainer implements Converter
{
    @Override
    public String getSource() { return "text/csv"; }

    @Override
    public String getTarget()
    {
        return "text/html";
    }

    public Trainer()
    {
    }

    public Trainer(Map<String, String> props)
    {
    }

    @Override
    public ConverterResult convert(byte[] source, String sourceEncoding,
                                   String sourceLanguage, String filename)
        throws UnsupportedEncodingException
    {
        String html = "";

        ConverterResult result = new ConverterResult(
            html.getBytes(Charset.forName("UTF-8")), filename, sourceEncoding);

        return result;
    }
}
