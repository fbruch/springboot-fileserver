package de.codereview.springboot.fileserver.service.plugin;

public interface Converter
{
    String getSource();

    String getTarget();

    ConverterResult convert(byte[] source, String filename);
}
