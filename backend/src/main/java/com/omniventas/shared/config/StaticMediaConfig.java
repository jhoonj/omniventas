// com/omniventas/shared/config/StaticMediaConfig.java
package com.omniventas.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class StaticMediaConfig implements WebFluxConfigurer {

    @Value("${app.media-dir}")
    String mediaDir;

    @Value("${app.media-base-url:/media}")
    String mediaBaseUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Sirve /omniventas/media/** desde el filesystem
        String location = "file:" + (mediaDir.endsWith("/") ? mediaDir : mediaDir + "/");
        registry.addResourceHandler(mediaBaseUrl + "/**")
                .addResourceLocations(location);
    }
}
