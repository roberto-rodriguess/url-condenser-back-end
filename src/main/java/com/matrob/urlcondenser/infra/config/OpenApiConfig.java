package com.matrob.urlcondenser.infra.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()

                .info(new Info()

                        .title("URL Condenser API")

                        .description("""
                                API REST desenvolvida em Spring Boot para encurtamento de URLs.

                                Funcionalidades:
                                • Criar URL curta
                                • Redirecionar URL
                                • Consultar estatísticas
                                • Listar URLs
                                • Excluir URLs
                                """)

                        .version("1.0.0")

                        .contact(new Contact()
                                .name("Mateus Azevedo")
                                .email("mateus@email.com")
                                .url("https://github.com/seu-github"))

                        .license(new License()
                                .name("MIT")))

                .externalDocs(new ExternalDocumentation()
                        .description("Repositório do Projeto")
                        .url("https://github.com/seu-github/urlcondenser"));

    }

}
