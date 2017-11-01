package de.codereview.springboot.fileserver.service.plugin;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "fileserver.plugin")
public class PluginProperties
{
    private Map<String, String> asciidoctor = new HashMap<>();

    private List<ConverterConfig> converters = new ArrayList<>();

    public Map<String, String> getAsciidoctor()
    {
        return asciidoctor;
    }

    public List<ConverterConfig> getConverters()
    {
        return converters;
    }
}
