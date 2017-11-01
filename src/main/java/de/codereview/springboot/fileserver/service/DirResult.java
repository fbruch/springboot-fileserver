package de.codereview.springboot.fileserver.service;

import java.util.List;

public class DirResult extends FileResult
{
    private List<FileResult> files;

    public DirResult(String box, String path, String filename, List<FileResult> files)
    {
        super(box, path, filename, true);
        this.files = files;
    }

    public List<FileResult> getFiles()
    {
        return files;
    }

    public void setFiles(List<FileResult> files)
    {
        this.files = files;
    }
}
