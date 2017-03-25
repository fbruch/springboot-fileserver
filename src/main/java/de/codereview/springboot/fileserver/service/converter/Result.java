package de.codereview.springboot.fileserver.service.converter;

public class Result
{
    private String title;
    private byte[] content;

    public Result(byte[] content, String title)
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
