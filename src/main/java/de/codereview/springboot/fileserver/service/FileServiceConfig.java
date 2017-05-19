package de.codereview.springboot.fileserver.service;

import org.apache.commons.lang.LocaleUtils;
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
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    @Validated
    static public class Root implements Cloneable
    {
        @NotBlank
        private String name;

        @NotBlank
//        @URL(protocol="file", host="") // does not work, the java.net.URL-constructor does...
        @CheckPath
        private String path;

        @CheckEncoding(optional=true)
        private String encoding;

        @CheckLanguage(optional=true)
        private String language;

        @SuppressWarnings("unused")
        public Root()
        {
        }

        Root(String name, String path)
        {
            this.name = name;
            this.path = path;
        }

        @Override
        public Object clone() {
            Root clone = new Root();
            clone.setName(this.name);
            clone.setPath(this.path);
            clone.setEncoding(this.encoding);
            clone.setLanguage(this.language);
            return clone;
        }

        public String getName() { return name; }

        public void setName(String name) { this.name = name; }

        public String getPath() { return path; }

        public void setPath(String path) { this.path = path; }

        public String getEncoding() { return encoding; }

        public void setEncoding(String encoding) { this.encoding = encoding; }

        public String getLanguage()
        {
            return language;
        }

        public void setLanguage(String language)
        {
            this.language = language;
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
    @Constraint(validatedBy = CheckEncodingValidator.class)
    @interface CheckEncoding
    {
        String message() default "Unknown encoding for Java Runtime";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};

        boolean optional() default false;
    }

    private static class CheckEncodingValidator implements ConstraintValidator<CheckEncoding, String>
    {
        private boolean optional;
        @Override
        public void initialize(CheckEncoding constraintAnnotation)
        {
            optional = constraintAnnotation.optional();
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context)
        {
            return validate(value);
        }

        private boolean validate(String value)
        {
            if (value==null) return optional;
            if (StringUtils.isEmpty(value)) return false;
            Charset charset = Charset.forName(value);
            boolean isRegistered = charset.isRegistered();
            return isRegistered;
        }
    }

    @Target({FIELD})
    @Retention(RUNTIME)
    @Constraint(validatedBy = CheckLanguageValidator.class)
    @interface CheckLanguage
    {
        String message() default "Unknown language (locale) for Java Runtime";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};

        boolean optional() default false;
    }

    private static class CheckLanguageValidator implements ConstraintValidator<CheckLanguage, String>
    {
        private boolean optional;
        @Override
        public void initialize(CheckLanguage constraintAnnotation)
        {
            optional = constraintAnnotation.optional();
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context)
        {
            return validate(value);
        }

        private boolean validate(String value)
        {
            if (value==null) return optional;
            if (StringUtils.isEmpty(value)) return false;
            boolean isValid;
            try {
//            Locale locale = Locale.forLanguageTag(value);
                Locale locale = LocaleUtils.toLocale(value);
                isValid = locale.getVariant() != null;
            } catch (IllegalArgumentException e) {
                isValid = false;
            }
            return isValid;
        }
    }

}
