package de.codereview.springboot.fileserver.service.converter;

public interface Converter
{
    String getSource();

    String getTarget();

    Result convert(byte[] source, String filename);
}
