package de.codereview.springboot.fileserver.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileResult
{
    private String box;
    private String parentPath;
    private String filename;
    private String encoding;
    private String language;

    private Map<String, String> header = new HashMap<>();

    @JsonIgnore
    private boolean textual;
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

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    public Map<String, String> getHeader()
    {
        return header;
    }

    public void setHeader(Map<String, String> header)
    {
        this.header = header;
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
