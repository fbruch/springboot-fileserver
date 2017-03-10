package de.codereview.springboot.fileserver;

public interface Converter
{
    String getSource();

    String getTarget();

    byte[] convert(byte[] source, String title);
}
