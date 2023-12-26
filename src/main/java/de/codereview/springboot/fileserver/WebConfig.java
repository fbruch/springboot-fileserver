package de.codereview.springboot.fileserver;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebConfig implements WebMvcConfigurer
{
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new BlockRemoteInterceptor());
    }

    @Override
    public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false).
            favorParameter(false).
            ignoreAcceptHeader(true).
            useJaf(false).
            defaultContentType(MediaType.TEXT_HTML);
    }
}
