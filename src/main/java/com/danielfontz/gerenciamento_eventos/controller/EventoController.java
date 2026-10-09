package com.danielfontz.gerenciamento_eventos.controller;

import com.danielfontz.gerenciamento_eventos.assembler.EventoModelAssembler;
import com.danielfontz.gerenciamento_eventos.model.Evento;
import com.danielfontz.gerenciamento_eventos.model.StatusEvento;
import com.danielfontz.gerenciamento_eventos.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller responsável por todas as operações relacionadas a Eventos.
 * Expõe 8 endpoints RESTful documentados com OpenAPI/Swagger.
 */
@RestController
@RequestMapping("/eventos")
@Tag(name = "Eventos", description = "Operações para gerenciamento completo de eventos")
public class EventoController {

    private final EventoService service;
    private final EventoModelAssembler assembler;
    private final PagedResourcesAssembler<Evento> pagedResourcesAssembler;

    public EventoController(EventoService service,
                            EventoModelAssembler assembler,
                            PagedResourcesAssembler<Evento> pagedResourcesAssembler) {
        this.service = service;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    // ===================================================================
    // 1. GET /eventos — LISTAR (paginado)
    // ===================================================================
    @Operation(
            summary = "Lista todos os eventos (paginado)",
            description = """
            Retorna uma lista paginada de todos os eventos cadastrados no sistema.
            
            **Parâmetros de paginação:**
            - `page`: Número da página (começa em 0). Padrão: 0.
            - `size`: Quantidade de itens por página. Padrão: 10.
            - `sort`: Campo de ordenação. Padrão: id, asc.
            
            **Exemplo de requisição:**
            `GET /eventos?page=0&size=5&sort=data,asc`
            
            **HATEOAS:** a resposta contém links `_links` para `self`, `next`, `prev`, `first` e `last`.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista paginada retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo de resposta paginada",
                                    value = """
                        {
                          "_embedded": {
                            "eventoList": [
                              {
                                "id": 1,
                                "titulo": "Workshop de Spring Boot",
                                "data": "2026-11-09",
                                "status": "PLANEJADO",
                                "local": { "id": 1, "nome": "Centro de Convenções", "cidade": "São Paulo" },
                                "palestrantes": [ { "id": 1, "nome": "Ana Silva" } ],
                                "_links": {
                                  "self": { "href": "http://localhost:8080/eventos/1" },
                                  "eventos": { "href": "http://localhost:8080/eventos" },
                                  "update": { "href": "http://localhost:8080/eventos/1" },
                                  "delete": { "href": "http://localhost:8080/eventos/1" }
                                }
                              }
                            ]
                          },
                          "_links": {
                            "self": { "href": "http://localhost:8080/eventos?page=0&size=5" },
                            "first": { "href": "http://localhost:8080/eventos?page=0&size=5" },
                            "last": { "href": "http://localhost:8080/eventos?page=0&size=5" }
                          },
                          "page": { "size": 5, "totalElements": 3, "totalPages": 1, "number": 0 }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Evento>>> getAllEventos(
            @ParameterObject
            @PageableDefault(size = 10, page = 0, sort = "id")
            Pageable pageable) {
        Page<Evento> page = service.listar(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    // ===================================================================
    // 2. GET /eventos/{id} — BUSCAR POR ID
    // ===================================================================
    @Operation(
            summary = "Busca um evento pelo ID",
            description = """
            Retorna os detalhes completos de um evento específico, incluindo:
            - Local (One-to-One)
            - Palestrantes (Many-to-Many)
            - Links HATEOAS para navegação
            
            **Exemplo de requisição:**
            `GET /eventos/1`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Evento encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Evento.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de evento completo",
                                    value = """
                        {
                          "id": 1,
                          "titulo": "Workshop de Spring Boot",
                          "data": "2026-11-09",
                          "status": "PLANEJADO",
                          "local": {
                            "id": 1,
                            "nome": "Centro de Convenções",
                            "endereco": "Av. Paulista, 1000",
                            "cidade": "São Paulo"
                          },
                          "palestrantes": [
                            { "id": 1, "nome": "Ana Silva", "especialidade": "Java" },
                            { "id": 2, "nome": "Bruno Costa", "especialidade": "Spring Boot" }
                          ],
                          "_links": {
                            "self": { "href": "http://localhost:8080/eventos/1" },
                            "eventos": { "href": "http://localhost:8080/eventos" },
                            "update": { "href": "http://localhost:8080/eventos/1" },
                            "delete": { "href": "http://localhost:8080/eventos/1" }
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Evento não encontrado com o ID informado",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Não foi possível encontrar o evento com id 999")
                    )
            )
    })
    @GetMapping("/{id}")
    public EntityModel<Evento> getEventoById(
            @Parameter(
                    description = "ID do evento a ser buscado",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        return assembler.toModel(service.buscarPorId(id));
    }

    // ===================================================================
    // 3. POST /eventos — CRIAR
    // ===================================================================
    @Operation(
            summary = "Cria um novo evento",
            description = """
            Cadastra um novo evento no sistema.
            
            **Regras de negócio:**
            - `titulo`: obrigatório, não pode ser vazio.
            - `data`: obrigatória, deve ser hoje ou data futura (`@FutureOrPresent`).
            - `status`: obrigatório. Valores válidos: PLANEJADO, CONFIRMADO, EM_ANDAMENTO, CONCLUIDO, CANCELADO.
            - Não é permitido cadastrar dois eventos com o **mesmo título na mesma data**.
            
            **Exemplo de requisição:**
            `POST /eventos`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Evento criado com sucesso",
                    headers = @Header(
                            name = "Location",
                            description = "URL do evento recém-criado",
                            schema = @Schema(type = "string", example = "http://localhost:8080/eventos/4")
                    ),
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Evento criado",
                                    value = """
                        {
                          "id": 4,
                          "titulo": "DevOps Day",
                          "data": "2026-12-10",
                          "status": "PLANEJADO",
                          "_links": {
                            "self": { "href": "http://localhost:8080/eventos/4" },
                            "eventos": { "href": "http://localhost:8080/eventos" },
                            "update": { "href": "http://localhost:8080/eventos/4" },
                            "delete": { "href": "http://localhost:8080/eventos/4" }
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload inválido (validação falhou) ou evento duplicado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-10-09T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "path": "/eventos"
                    }
                    """)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<?> createEvento(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do evento a ser criado",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Evento.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Evento completo",
                                            summary = "Exemplo com todos os campos",
                                            value = """
                                {
                                  "titulo": "DevOps Day",
                                  "data": "2026-12-10",
                                  "status": "PLANEJADO"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Evento mínimo",
                                            summary = "Apenas campos obrigatórios",
                                            value = """
                                {
                                  "titulo": "Meetup DevOps",
                                  "data": "2026-12-15",
                                  "status": "PLANEJADO"
                                }
                                """
                                    )
                            }
                    )
            )
            @RequestBody @Valid Evento newEvento) {

        EntityModel<Evento> entityModel = assembler.toModel(service.criar(newEvento));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    // ===================================================================
    // 4. PUT /eventos/{id} — ATUALIZAR
    // ===================================================================
    @Operation(
            summary = "Atualiza um evento existente",
            description = """
            Atualiza todos os campos de um evento. Se o ID não existir, cria um novo.
            
            **Exemplo de requisição:**
            `PUT /eventos/1`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Evento atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "titulo": "Workshop de Spring Boot 4",
                      "data": "2026-11-20",
                      "status": "CONFIRMADO",
                      "_links": { "self": { "href": "http://localhost:8080/eventos/1" } }
                    }
                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Evento>> updateOrCreateEvento(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Novos dados do evento",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Evento.class),
                            examples = @ExampleObject(value = """
                        {
                          "titulo": "Workshop de Spring Boot 4",
                          "data": "2026-11-20",
                          "status": "CONFIRMADO"
                        }
                        """)
                    )
            )
            @RequestBody @Valid Evento newEvento,
            @Parameter(description = "ID do evento a ser atualizado", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(service.atualizarOuCriar(id, newEvento)));
    }

    // ===================================================================
    // 5. DELETE /eventos/{id} — DELETAR
    // ===================================================================
    @Operation(
            summary = "Deleta um evento",
            description = """
            Remove permanentemente um evento do sistema.
            
            **Exemplo de requisição:**
            `DELETE /eventos/1`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evento deletado com sucesso", content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Evento não encontrado",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Não foi possível encontrar o evento com id 999")
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvento(
            @Parameter(description = "ID do evento a ser deletado", example = "1", required = true)
            @PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // ===================================================================
    // 6. GET /eventos/buscar — CONSULTA PERSONALIZADA (por título)
    // ===================================================================
    @Operation(
            summary = "Busca eventos por título",
            description = """
            Retorna uma lista paginada de eventos cujo título contenha o termo informado 
            (busca case-insensitive).
            
            **Exemplo de requisição:**
            `GET /eventos/buscar?titulo=Spring&page=0&size=10`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista filtrada retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetro 'titulo' não informado", content = @Content)
    })
    @GetMapping("/buscar")
    public ResponseEntity<PagedModel<EntityModel<Evento>>> buscarPorTitulo(
            @Parameter(description = "Termo a ser buscado no título do evento", example = "Spring", required = true)
            @RequestParam String titulo,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Evento> page = service.buscarPorTitulo(titulo, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    // ===================================================================
    // 7. GET /eventos/status/{status} — CONSULTA PERSONALIZADA (por status)
    // ===================================================================
    @Operation(
            summary = "Busca eventos por status",
            description = """
            Retorna uma lista paginada de eventos filtrados por status.
            
            **Valores válidos para status:**
            - `PLANEJADO`
            - `CONFIRMADO`
            - `EM_ANDAMENTO`
            - `CONCLUIDO`
            - `CANCELADO`
            
            **Exemplo de requisição:**
            `GET /eventos/status/CONFIRMADO`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista filtrada retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status inválido", content = @Content)
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<PagedModel<EntityModel<Evento>>> buscarPorStatus(
            @Parameter(
                    description = "Status do evento",
                    example = "CONFIRMADO",
                    required = true,
                    schema = @Schema(implementation = StatusEvento.class)
            )
            @PathVariable StatusEvento status,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Evento> page = service.buscarPorStatus(status, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    // ===================================================================
    // 8. GET /eventos/cidade/{cidade} — CONSULTA PERSONALIZADA (por cidade)
    // ===================================================================
    @Operation(
            summary = "Busca eventos por cidade do local",
            description = """
            Retorna uma lista paginada de eventos cujo local esteja na cidade informada.
            
            **Exemplo de requisição:**
            `GET /eventos/cidade/São Paulo`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista filtrada retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetro 'cidade' não informado", content = @Content)
    })
    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<PagedModel<EntityModel<Evento>>> buscarPorCidade(
            @Parameter(description = "Nome da cidade", example = "São Paulo", required = true)
            @PathVariable String cidade,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Evento> page = service.buscarPorCidade(cidade, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    // ===================================================================
    // 9. GET /eventos/periodo — CONSULTA PERSONALIZADA (por intervalo de datas)
    // ===================================================================
    @Operation(
            summary = "Busca eventos por intervalo de datas",
            description = """
            Retorna uma lista paginada de eventos cuja data esteja entre os valores informados.
            
            **Formato das datas:** ISO-8601 (`yyyy-MM-dd`)
            
            **Exemplo de requisição:**
            `GET /eventos/periodo?inicio=2026-11-01&fim=2026-12-31`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista filtrada retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Datas inválidas ou formato incorreto", content = @Content)
    })
    @GetMapping("/periodo")
    public ResponseEntity<PagedModel<EntityModel<Evento>>> buscarPorIntervalo(
            @Parameter(
                    description = "Data inicial (formato ISO: yyyy-MM-dd)",
                    example = "2026-11-01",
                    required = true
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @Parameter(
                    description = "Data final (formato ISO: yyyy-MM-dd)",
                    example = "2026-12-31",
                    required = true
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Evento> page = service.buscarPorIntervalo(inicio, fim, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }
}