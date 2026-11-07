package it.uniroma3.siw.wefit.configuration;

import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Value("${wefit.upload-dir}")
    private String cartellaFoto;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String posizione = Path.of(cartellaFoto).toAbsolutePath().normalize().toUri().toString();
        if (!posizione.endsWith("/")) {
            posizione += "/";
        }
        registry.addResourceHandler("/uploads/esercizi/**").addResourceLocations(posizione);
    }
}
