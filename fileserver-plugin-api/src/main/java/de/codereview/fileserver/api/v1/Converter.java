package de.codereview.fileserver.api.v1;

import java.io.UnsupportedEncodingException;

public interface Converter
{
    String getSource();

    String getTarget();

    ConverterResult convert(byte[] source, String sourceEncoding, String sourceLanguage, String filename) throws UnsupportedEncodingException;
}
