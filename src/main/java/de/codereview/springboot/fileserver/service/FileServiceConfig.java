package de.codereview.springboot.fileserver.service;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "fileserver.box")
@Validated
public class FileServiceConfig
{
    @NotEmpty
    private List<Root> roots = new ArrayList<>();

    public List<Root> getRoots()
    {
        return roots;
    }

    static public class Root
    {
        @NotBlank
        private String name;
        @NotBlank
        private String path;

        @SuppressWarnings("unused")
        public Root()
        {
        }

        Root(String name, String path)
        {
            this.name = name;
            this.path = path;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getPath()
        {
            return path;
        }

        public void setPath(String path)
        {
            this.path = path;
        }
    }

}
