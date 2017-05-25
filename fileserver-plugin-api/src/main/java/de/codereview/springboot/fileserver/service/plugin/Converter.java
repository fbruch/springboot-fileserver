package de.codereview.springboot.fileserver.service.plugin;

import java.io.UnsupportedEncodingException;

public interface Converter
{
    String getSource();

    String getTarget();

    ConverterResult convert(byte[] source, String sourceEncoding, String sourceLanguage, String filename) throws UnsupportedEncodingException;
}
