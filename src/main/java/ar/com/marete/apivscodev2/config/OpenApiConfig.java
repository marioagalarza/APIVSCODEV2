package ar.com.marete.apivscodev2.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("APIVSCODEV2")
                        .description("API de ejemplo con Spring Boot 3.x")
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact().email("mario.alberto.galarza@gmail.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local"),
                        new Server().url("http://api-npe.marete.com.ar").description("NPE"),
                        new Server().url("http://api-uat.marete.com.ar").description("UAT"),
                        new Server().url("https://api.marete.com.ar").description("PROD")));
    }
}
