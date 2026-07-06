package ee.jaakobjaan.signing.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI signingRequestServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Signing Request Service API")
                        .version("0.1.0")
                        .description("REST API for a digital signing request workflow demo.")
                        .contact(new Contact()
                                .name("Jaakob-Jaan Avvo")));
    }
}