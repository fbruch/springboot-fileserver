package de.codereview.springboot.fileserver.service.plugin;

import java.util.Map;

public class ConverterConfig
{
    private String classname;
    private Map<String,String> config;

    public String getClassname()
    {
        return classname;
    }

    public void setClassname(String classname)
    {
        this.classname = classname;
    }

    public Map<String, String> getConfig()
    {
        return config;
    }

    public void setConfig(Map<String, String> config)
    {
        this.config = config;
    }
}
