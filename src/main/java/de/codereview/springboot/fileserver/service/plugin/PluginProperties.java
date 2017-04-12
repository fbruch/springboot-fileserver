package de.codereview.springboot.fileserver.service.plugin;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix="fileserver.plugin")
public class PluginProperties
{
    private Map<String, String> asciidoctor = new HashMap<>();

    @Bean
    public Map<String, String> getAsciidoctor()
    {
        return asciidoctor;
    }
}
