package com.danielfontz.gerenciamento_eventos.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração global do OpenAPI Document (lido pelo Swagger UI).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gerenciamentoEventosOpenAPI() {

        Info info = new Info()
                .title("API de Gerenciamento de Eventos")
                .version("1.0.0")
                .description("""
                        ## Visão Geral
                        
                        API RESTful para gerenciamento de eventos, locais, palestrantes, 
                        participantes e inscrições.
                        
                        ## Recursos
                        
                        - **Eventos** — CRUD + busca por título, status, cidade e período
                        - **Locais** — CRUD + busca por cidade, nome e endereço
                        - **Palestrantes** — CRUD + busca por nome e especialidade
                        - **Participantes** — CRUD + busca por nome
                        - **Inscrições** — CRUD + busca por status, evento, participante e período
                        
                        ## Padrões
                        
                        - Spring Boot 4.1.1 + Java 27
                        - Spring Data JPA + H2
                        - HATEOAS (links `self`, `update`, `delete`)
                        - Paginação (`page`, `size`, `sort`)
                        - Bean Validation
                        
                        ## Códigos HTTP
                        
                        `200 OK` · `201 Created` · `204 No Content` · `400 Bad Request` · `404 Not Found`
                        """)
                .contact(new Contact()
                        .name("Daniel David Fuentes")
                        .email("danielfontz056@gmail.com")
                        .url("https://github.com/danielfontz"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));

        List<Server> servers = List.of(
                new Server()
                        .url("http://localhost:8080")
                        .description("Servidor de Desenvolvimento")
        );

        List<Tag> tags = List.of(
                new Tag().name("Eventos").description("Gerenciamento de eventos"),
                new Tag().name("Locais").description("Gerenciamento de locais"),
                new Tag().name("Palestrantes").description("Gerenciamento de palestrantes"),
                new Tag().name("Participantes").description("Gerenciamento de participantes"),
                new Tag().name("Inscrições").description("Gerenciamento de inscrições")
        );

        ExternalDocumentation externalDocs = new ExternalDocumentation()
                .description("Repositório do projeto")
                .url("https://github.com/danielfontz/gerenciamento-eventos");

        return new OpenAPI()
                .info(info)
                .servers(servers)
                .tags(tags)
                .externalDocs(externalDocs);
    }
}