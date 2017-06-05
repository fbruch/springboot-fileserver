package de.codereview.fileserver.plugin.trainer;

import de.codereview.fileserver.api.v1.Converter;
import de.codereview.fileserver.api.v1.ConverterResult;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

public class Viewer implements Converter
{
    @Override
    public String getSource() { return "text/csv"; }

    @Override
    public String getTarget()
    {
        return "text/adoc";
    }

    public Viewer()
    {
    }

    public Viewer(Map<String, String> props)
    {
    }

    @Override
    public ConverterResult convert(byte[] source, String sourceEncoding,
                                   String sourceLanguage, String filename)
        throws UnsupportedEncodingException
    {
        String adoc = "";

        ConverterResult result = new ConverterResult(
            adoc.getBytes(Charset.forName("UTF-8")), filename, sourceEncoding);

        return result;
    }
}
