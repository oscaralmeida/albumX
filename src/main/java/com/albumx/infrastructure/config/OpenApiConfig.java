package com.albumx.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI albumXOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("AlbumX API — MVP Troca de Figurinhas")
                        .description("API REST do MVP para cadastro, coleção, repetidas e propostas de troca.")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Desenvolvimento local / Docker Compose"),
                        new Server().url("http://backend:8080").description("Rede interna Docker Compose")
                ));
    }
}
