package de.codereview.springboot.fileserver.service;

import java.util.Map;

public class FileResult
{
    private String box;
    private String parentPath;
    private String filename;
    private String mimeType;
    private boolean textual;
    private String encoding;
    private byte[] content;
    private Map<String, String> metadata;
    private String language;
    private boolean directory;

    public FileResult(String box, String path, String filename, boolean directory)
    {
        this.box = box;
        this.parentPath = path;
        this.filename = filename;
        this.directory = directory;
    }

    public String getParentPath()
    {
        return parentPath;
    }

    public void setParentPath(String path)
    {
        this.parentPath = path;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    public byte[] getContent()
    {
        return content;
    }

    public void setContent(byte[] content)
    {
        this.content = content;
    }

    public Map<String, String> getMetadata()
    {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata)
    {
        this.metadata = metadata;
    }

    public boolean isTextual()
    {
        return textual;
    }

    public void setTextual(boolean textual)
    {
        this.textual = textual;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setDirectory(boolean directory)
    {
        this.directory = directory;
    }

    public boolean isDirectory()
    {
        return directory;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getBox()
    {
        return box;
    }

    public void setBox(String box)
    {
        this.box = box;
    }
}
