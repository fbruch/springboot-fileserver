package de.codereview.springboot.fileserver.service.converter;

public interface Converter
{
    String getSource();

    String getTarget();

    byte[] convert(byte[] source, String title);
}
