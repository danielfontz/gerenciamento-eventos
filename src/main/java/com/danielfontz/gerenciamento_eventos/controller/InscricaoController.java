package com.danielfontz.gerenciamento_eventos.controller;

import com.danielfontz.gerenciamento_eventos.assembler.InscricaoModelAssembler;
import com.danielfontz.gerenciamento_eventos.model.Inscricao;
import com.danielfontz.gerenciamento_eventos.service.InscricaoService;
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

import java.time.LocalDateTime;

/**
 * Controller responsável por todas as operações relacionadas a Inscrições.
 * Expõe 9 endpoints RESTful documentados com OpenAPI/Swagger.
 *
 * Esta é a entidade associativa entre Evento e Participante — possui dois @ManyToOne.
 */
@RestController
@RequestMapping("/inscricoes")
@Tag(name = "Inscrições", description = "Operações para gerenciamento de inscrições (associação entre Evento e Participante)")
public class InscricaoController {

    private final InscricaoService service;
    private final InscricaoModelAssembler assembler;
    private final PagedResourcesAssembler<Inscricao> pagedResourcesAssembler;

    public InscricaoController(InscricaoService service,
                               InscricaoModelAssembler assembler,
                               PagedResourcesAssembler<Inscricao> pagedResourcesAssembler) {
        this.service = service;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    // ===================================================================
    // 1. GET /inscricoes — LISTAR (paginado)
    // ===================================================================
    @Operation(
            summary = "Lista todas as inscrições (paginado)",
            description = """
            Retorna uma lista paginada de todas as inscrições cadastradas no sistema.
            
            Cada inscrição traz o `evento` e o `participante` completos aninhados, 
            além de links HATEOAS para navegar até eles.
            
            **Parâmetros de paginação:**
            - `page`: Número da página (começa em 0). Padrão: 0.
            - `size`: Quantidade de itens por página. Padrão: 10.
            - `sort`: Campo de ordenação (ex: `dataInscricao`). Padrão: id, asc.
            
            **Exemplo de requisição:**
            `GET /inscricoes?page=0&size=5&sort=dataInscricao,desc`
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
                            "inscricaoList": [
                              {
                                "id": 1,
                                "dataInscricao": "2026-10-09T14:00:00",
                                "status": "ATIVA",
                                "evento": {
                                  "id": 1,
                                  "titulo": "Workshop de Spring Boot",
                                  "data": "2026-11-09",
                                  "status": "PLANEJADO"
                                },
                                "participante": {
                                  "id": 1,
                                  "nome": "João Souza",
                                  "email": "joao@exemplo.com",
                                  "telefone": "(11) 99999-0000"
                                },
                                "_links": {
                                  "self": { "href": "http://localhost:8080/inscricoes/1" },
                                  "inscricoes": { "href": "http://localhost:8080/inscricoes" },
                                  "update": { "href": "http://localhost:8080/inscricoes/1" },
                                  "delete": { "href": "http://localhost:8080/inscricoes/1" },
                                  "evento": { "href": "http://localhost:8080/eventos/1" },
                                  "participante": { "href": "http://localhost:8080/participantes/1" }
                                }
                              }
                            ]
                          },
                          "_links": {
                            "self": { "href": "http://localhost:8080/inscricoes?page=0&size=5" },
                            "first": { "href": "http://localhost:8080/inscricoes?page=0&size=5" },
                            "last": { "href": "http://localhost:8080/inscricoes?page=0&size=5" }
                          },
                          "page": { "size": 5, "totalElements": 4, "totalPages": 1, "number": 0 }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Inscricao>>> getAllInscricoes(
            @ParameterObject
            @PageableDefault(size = 10, page = 0, sort = "id")
            Pageable pageable) {
        Page<Inscricao> page = service.listar(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    // ===================================================================
    // 2. GET /inscricoes/{id} — BUSCAR POR ID
    // ===================================================================
    @Operation(
            summary = "Busca uma inscrição pelo ID",
            description = """
            Retorna os detalhes completos de uma inscrição específica, incluindo o evento 
            e o participante associados.
            
            **HATEOAS — navegabilidade entre recursos:**
            Os links `evento` e `participante` permitem navegar diretamente até os 
            recursos relacionados.
            
            **Exemplo de requisição:**
            `GET /inscricoes/1`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inscrição encontrada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Inscricao.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de inscrição",
                                    value = """
                        {
                          "id": 1,
                          "dataInscricao": "2026-10-09T14:00:00",
                          "status": "ATIVA",
                          "evento": {
                            "id": 1,
                            "titulo": "Workshop de Spring Boot",
                            "data": "2026-11-09",
                            "status": "PLANEJADO"
                          },
                          "participante": {
                            "id": 1,
                            "nome": "João Souza",
                            "email": "joao@exemplo.com",
                            "telefone": "(11) 99999-0000"
                          },
                          "_links": {
                            "self": { "href": "http://localhost:8080/inscricoes/1" },
                            "inscricoes": { "href": "http://localhost:8080/inscricoes" },
                            "update": { "href": "http://localhost:8080/inscricoes/1" },
                            "delete": { "href": "http://localhost:8080/inscricoes/1" },
                            "evento": { "href": "http://localhost:8080/eventos/1" },
                            "participante": { "href": "http://localhost:8080/participantes/1" }
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Inscrição não encontrada com o ID informado",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Não foi possível encontrar a inscrição com id 999")
                    )
            )
    })
    @GetMapping("/{id}")
    public EntityModel<Inscricao> getInscricaoById(
            @Parameter(
                    description = "ID da inscrição a ser buscada",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        return assembler.toModel(service.buscarPorId(id));
    }

    // ===================================================================
    // 3. POST /inscricoes — CRIAR
    // ===================================================================
    @Operation(
            summary = "Cria uma nova inscrição",
            description = """
            Cadastra uma nova inscrição associando um evento a um participante.
            
            **Regras de negócio:**
            - `evento.id`: deve existir no sistema (senão retorna 404).
            - `participante.id`: deve existir no sistema (senão retorna 404).
            - **Não é permitido** o mesmo participante se inscrever duas vezes no mesmo evento.
            - Se `dataInscricao` não for informada, o sistema usa a data/hora atual.
            - `status`: obrigatório (ex: ATIVA, PENDENTE, CANCELADA).
            
            **Exemplo de requisição:**
            `POST /inscricoes`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Inscrição criada com sucesso",
                    headers = @Header(
                            name = "Location",
                            description = "URL da inscrição recém-criada",
                            schema = @Schema(type = "string", example = "http://localhost:8080/inscricoes/5")
                    ),
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Inscrição criada",
                                    value = """
                        {
                          "id": 5,
                          "dataInscricao": "2026-11-01T10:00:00",
                          "status": "ATIVA",
                          "evento": { "id": 2, "titulo": "Conferência Java" },
                          "participante": { "id": 2, "nome": "Maria Oliveira" },
                          "_links": {
                            "self": { "href": "http://localhost:8080/inscricoes/5" },
                            "evento": { "href": "http://localhost:8080/eventos/2" },
                            "participante": { "href": "http://localhost:8080/participantes/2" }
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload inválido OU inscrição duplicada",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "O participante 2 já está inscrito no evento 2")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Evento ou participante não encontrado",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Não foi possível encontrar o evento com id 999")
                    )
            )
    })
    @PostMapping
    public ResponseEntity<?> createInscricao(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da inscrição a ser criada",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Inscricao.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Inscrição completa",
                                            summary = "Com data informada",
                                            value = """
                                {
                                  "dataInscricao": "2026-11-01T10:00:00",
                                  "status": "ATIVA",
                                  "evento": { "id": 1 },
                                  "participante": { "id": 1 }
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Inscrição sem data",
                                            summary = "Sistema usa data/hora atual",
                                            value = """
                                {
                                  "status": "PENDENTE",
                                  "evento": { "id": 2 },
                                  "participante": { "id": 3 }
                                }
                                """
                                    )
                            }
                    )
            )
            @RequestBody @Valid Inscricao newInscricao) {

        EntityModel<Inscricao> entityModel = assembler.toModel(service.criar(newInscricao));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    // ===================================================================
    // 4. PUT /inscricoes/{id} — ATUALIZAR
    // ===================================================================
    @Operation(
            summary = "Atualiza uma inscrição existente",
            description = """
            Atualiza todos os campos de uma inscrição. Se o ID não existir, cria uma nova.
            
            **Exemplo de requisição:**
            `PUT /inscricoes/1`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inscrição atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "dataInscricao": "2026-11-01T10:00:00",
                      "status": "CANCELADA",
                      "evento": { "id": 1 },
                      "participante": { "id": 1 },
                      "_links": { "self": { "href": "http://localhost:8080/inscricoes/1" } }
                    }
                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Inscricao>> updateOrCreateInscricao(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Novos dados da inscrição",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Inscricao.class),
                            examples = @ExampleObject(value = """
                        {
                          "dataInscricao": "2026-11-01T10:00:00",
                          "status": "CANCELADA",
                          "evento": { "id": 1 },
                          "participante": { "id": 1 }
                        }
                        """)
                    )
            )
            @RequestBody @Valid Inscricao newInscricao,
            @Parameter(description = "ID da inscrição a ser atualizada", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(service.atualizarOuCriar(id, newInscricao)));
    }

    // ===================================================================
    // 5. DELETE /inscricoes/{id} — DELETAR
    // ===================================================================
    @Operation(
            summary = "Deleta uma inscrição",
            description = """
            Remove permanentemente uma inscrição do sistema.
            
            **Exemplo de requisição:**
            `DELETE /inscricoes/1`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inscrição deletada com sucesso", content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Inscrição não encontrada",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Não foi possível encontrar a inscrição com id 999")
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInscricao(
            @Parameter(description = "ID da inscrição a ser deletada", example = "1", required = true)
            @PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // ===================================================================
    // 6. GET /inscricoes/status — CONSULTA PERSONALIZADA (por status)
    // ===================================================================
    @Operation(
            summary = "Busca inscrições por status",
            description = """
            Retorna uma lista paginada de inscrições filtradas por status 
            (busca case-insensitive).
            
            **Valores comuns de status:**
            - `ATIVA`
            - `PENDENTE`
            - `CANCELADA`
            
            **Exemplo de requisição:**
            `GET /inscricoes/status?status=ATIVA`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista filtrada retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetro 'status' não informado", content = @Content)
    })
    @GetMapping("/status")
    public ResponseEntity<PagedModel<EntityModel<Inscricao>>> buscarPorStatus(
            @Parameter(description = "Status da inscrição", example = "ATIVA", required = true)
            @RequestParam String status,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Inscricao> page = service.buscarPorStatus(status, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    // ===================================================================
    // 7. GET /inscricoes/evento/{eventoId} — CONSULTA PERSONALIZADA (por evento)
    // ===================================================================
    @Operation(
            summary = "Busca inscrições de um evento",
            description = """
            Retorna uma lista paginada de todas as inscrições associadas a um evento específico.
            
            Útil para saber **quem se inscreveu** em um dado evento.
            
            **Exemplo de requisição:**
            `GET /inscricoes/evento/1`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista filtrada retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado", content = @Content)
    })
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<PagedModel<EntityModel<Inscricao>>> buscarPorEvento(
            @Parameter(description = "ID do evento", example = "1", required = true)
            @PathVariable Long eventoId,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Inscricao> page = service.buscarPorEvento(eventoId, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    // ===================================================================
    // 8. GET /inscricoes/participante/{participanteId} — CONSULTA PERSONALIZADA
    // ===================================================================
    @Operation(
            summary = "Busca inscrições de um participante",
            description = """
            Retorna uma lista paginada de todas as inscrições feitas por um participante 
            específico.
            
            Útil para mostrar **em quais eventos** um dado participante está inscrito.
            
            **Exemplo de requisição:**
            `GET /inscricoes/participante/1`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista filtrada retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Participante não encontrado", content = @Content)
    })
    @GetMapping("/participante/{participanteId}")
    public ResponseEntity<PagedModel<EntityModel<Inscricao>>> buscarPorParticipante(
            @Parameter(description = "ID do participante", example = "1", required = true)
            @PathVariable Long participanteId,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Inscricao> page = service.buscarPorParticipante(participanteId, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    // ===================================================================
    // 9. GET /inscricoes/periodo — CONSULTA PERSONALIZADA (por intervalo de datas)
    // ===================================================================
    @Operation(
            summary = "Busca inscrições por intervalo de datas",
            description = """
            Retorna uma lista paginada de inscrições feitas entre as datas informadas.
            
            **Formato das datas:** ISO-8601 (`yyyy-MM-ddTHH:mm:ss`)
            
            **Exemplo de requisição:**
            `GET /inscricoes/periodo?inicio=2026-01-01T00:00:00&fim=2026-12-31T23:59:59`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista filtrada retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Datas inválidas ou formato incorreto", content = @Content)
    })
    @GetMapping("/periodo")
    public ResponseEntity<PagedModel<EntityModel<Inscricao>>> buscarPorIntervalo(
            @Parameter(
                    description = "Data inicial (formato ISO: yyyy-MM-ddTHH:mm:ss)",
                    example = "2026-01-01T00:00:00",
                    required = true
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(
                    description = "Data final (formato ISO: yyyy-MM-ddTHH:mm:ss)",
                    example = "2026-12-31T23:59:59",
                    required = true
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Inscricao> page = service.buscarPorIntervalo(inicio, fim, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }
}