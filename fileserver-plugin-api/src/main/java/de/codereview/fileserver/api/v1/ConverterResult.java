package de.codereview.fileserver.api.v1;

public class ConverterResult
{
    private String title;
    private byte[] content;
    private String encoding;

    public ConverterResult(byte[] content, String title, String encoding)
    {
        this.title = title;
        this.content = content;
        this.encoding = encoding;
    }

    public String getTitle()
    {
        return title;
    }

    public byte[] getContent()
    {
        return content;
    }

    public String getEncoding()
    {
        return encoding;
    }
}
