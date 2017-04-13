package de.codereview.springboot.fileserver.service;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.Valid;
import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Component
@ConfigurationProperties(prefix = "fileserver.box")
@Validated
public class FileServiceConfig
{
    @NotEmpty
    private List<Root> roots = new ArrayList<>();

    @Valid
    public List<Root> getRoots()
    {
        return roots;
    }

    static public class Root
    {
        @NotBlank
        private String name;
        @NotBlank
//        @URL(protocol="file", host="") // does not work, the java.net.URL-constructor does...
        @CheckPath
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

    private static class CheckPathValidator implements ConstraintValidator<CheckPath, String>
    {
        @Override
        public void initialize(CheckPath constraintAnnotation)
        {
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context)
        {
            return validate(value);
        }

        private boolean validate(String value)
        {
            if (StringUtils.isEmpty(value)) return false;
            File file = Paths.get(value).toFile();
            boolean isDirectory = file.isDirectory();
            boolean canRead = file.canRead();
            boolean canExecute = file.canExecute();
            return isDirectory && canRead && canExecute;
        }
    }

    @Target({FIELD})
    @Retention(RUNTIME)
    @Constraint(validatedBy = CheckPathValidator.class)
    @interface CheckPath
    {
        String message() default "Not an accessible file system directory path";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }
}
