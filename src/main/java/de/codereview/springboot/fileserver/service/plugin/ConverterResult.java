package de.codereview.springboot.fileserver.service.plugin;

public class ConverterResult
{
    private String title;
    private byte[] content;

    public ConverterResult(byte[] content, String title)
    {
        this.title = title;
        this.content = content;
    }

    public String getTitle()
    {
        return title;
    }

    public byte[] getContent()
    {
        return content;
    }
}
