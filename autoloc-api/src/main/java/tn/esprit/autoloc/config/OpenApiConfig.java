package tn.esprit.autoloc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI autoLocOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AutoLoc API")
                        .description("API REST pour le système de gestion de location de véhicules AutoLoc - ESPRIT")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("JERBI Amenallah")
                                .email("amenallah.jerbi@esprit.tn"))
                        .license(new License().name("Apache 2.0")));
    }
}
