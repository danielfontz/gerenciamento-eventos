package com.danielfontz.gerenciamento_eventos.controller;

import com.danielfontz.gerenciamento_eventos.assembler.ParticipanteModelAssembler;
import com.danielfontz.gerenciamento_eventos.model.Participante;
import com.danielfontz.gerenciamento_eventos.service.ParticipanteService;
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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável por todas as operações relacionadas a Participantes.
 * Expõe 6 endpoints RESTful documentados com OpenAPI/Swagger.
 */
@RestController
@RequestMapping("/participantes")
@Tag(name = "Participantes", description = "Operações para gerenciamento completo de participantes")
public class ParticipanteController {

    private final ParticipanteService service;
    private final ParticipanteModelAssembler assembler;
    private final PagedResourcesAssembler<Participante> pagedResourcesAssembler;

    public ParticipanteController(ParticipanteService service,
                                  ParticipanteModelAssembler assembler,
                                  PagedResourcesAssembler<Participante> pagedResourcesAssembler) {
        this.service = service;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    // ===================================================================
    // 1. GET /participantes — LISTAR (paginado)
    // ===================================================================
    @Operation(
            summary = "Lista todos os participantes (paginado)",
            description = """
            Retorna uma lista paginada de todos os participantes cadastrados no sistema.
            
            **Parâmetros de paginação:**
            - `page`: Número da página (começa em 0). Padrão: 0.
            - `size`: Quantidade de itens por página. Padrão: 10.
            - `sort`: Campo de ordenação (ex: `nome`, `email`). Padrão: id, asc.
            
            **Exemplo de requisição:**
            `GET /participantes?page=0&size=5&sort=nome,asc`
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
                            "participanteList": [
                              {
                                "id": 1,
                                "nome": "João Souza",
                                "email": "joao@exemplo.com",
                                "telefone": "(11) 99999-0000",
                                "_links": {
                                  "self": { "href": "http://localhost:8080/participantes/1" },
                                  "participantes": { "href": "http://localhost:8080/participantes" },
                                  "update": { "href": "http://localhost:8080/participantes/1" },
                                  "delete": { "href": "http://localhost:8080/participantes/1" }
                                }
                              },
                              {
                                "id": 2,
                                "nome": "Maria Oliveira",
                                "email": "maria@exemplo.com",
                                "telefone": "(21) 98888-1111",
                                "_links": {
                                  "self": { "href": "http://localhost:8080/participantes/2" },
                                  "participantes": { "href": "http://localhost:8080/participantes" },
                                  "update": { "href": "http://localhost:8080/participantes/2" },
                                  "delete": { "href": "http://localhost:8080/participantes/2" }
                                }
                              }
                            ]
                          },
                          "_links": {
                            "self": { "href": "http://localhost:8080/participantes?page=0&size=5" },
                            "first": { "href": "http://localhost:8080/participantes?page=0&size=5" },
                            "last": { "href": "http://localhost:8080/participantes?page=0&size=5" }
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
    public ResponseEntity<PagedModel<EntityModel<Participante>>> getAllParticipantes(
            @ParameterObject
            @PageableDefault(size = 10, page = 0, sort = "id")
            Pageable pageable) {
        Page<Participante> page = service.listar(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    // ===================================================================
    // 2. GET /participantes/{id} — BUSCAR POR ID
    // ===================================================================
    @Operation(
            summary = "Busca um participante pelo ID",
            description = """
            Retorna os detalhes completos de um participante específico.
            
            **Observação:** o campo `inscricoes` é ocultado para evitar referência circular 
            (um participante pode ter várias inscrições). Para ver as inscrições de um 
            participante, use `GET /inscricoes/participante/{id}`.
            
            **Exemplo de requisição:**
            `GET /participantes/1`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Participante encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Participante.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de participante",
                                    value = """
                        {
                          "id": 1,
                          "nome": "João Souza",
                          "email": "joao@exemplo.com",
                          "telefone": "(11) 99999-0000",
                          "_links": {
                            "self": { "href": "http://localhost:8080/participantes/1" },
                            "participantes": { "href": "http://localhost:8080/participantes" },
                            "update": { "href": "http://localhost:8080/participantes/1" },
                            "delete": { "href": "http://localhost:8080/participantes/1" }
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Participante não encontrado com o ID informado",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Não foi possível encontrar o participante com id 999")
                    )
            )
    })
    @GetMapping("/{id}")
    public EntityModel<Participante> getParticipanteById(
            @Parameter(
                    description = "ID do participante a ser buscado",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        return assembler.toModel(service.buscarPorId(id));
    }

    // ===================================================================
    // 3. POST /participantes — CRIAR
    // ===================================================================
    @Operation(
            summary = "Cria um novo participante",
            description = """
            Cadastra um novo participante no sistema.
            
            **Regras de negócio:**
            - `nome`: obrigatório, no máximo 100 caracteres.
            - `email`: obrigatório, formato de e-mail válido, **único** no sistema.
            - `telefone`: opcional, no máximo 20 caracteres.
            
            **Exemplo de requisição:**
            `POST /participantes`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Participante criado com sucesso",
                    headers = @Header(
                            name = "Location",
                            description = "URL do participante recém-criado",
                            schema = @Schema(type = "string", example = "http://localhost:8080/participantes/4")
                    ),
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Participante criado",
                                    value = """
                        {
                          "id": 4,
                          "nome": "Fernanda Alves",
                          "email": "fernanda@exemplo.com",
                          "telefone": "(51) 95555-4444",
                          "_links": {
                            "self": { "href": "http://localhost:8080/participantes/4" },
                            "participantes": { "href": "http://localhost:8080/participantes" },
                            "update": { "href": "http://localhost:8080/participantes/4" },
                            "delete": { "href": "http://localhost:8080/participantes/4" }
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload inválido (validação falhou) ou e-mail duplicado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-10-09T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "path": "/participantes"
                    }
                    """)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<?> createParticipante(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do participante a ser criado",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Participante.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Participante completo",
                                            summary = "Exemplo com todos os campos",
                                            value = """
                                {
                                  "nome": "Fernanda Alves",
                                  "email": "fernanda@exemplo.com",
                                  "telefone": "(51) 95555-4444"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Participante mínimo",
                                            summary = "Sem telefone",
                                            value = """
                                {
                                  "nome": "Gabriel Rocha",
                                  "email": "gabriel@exemplo.com"
                                }
                                """
                                    )
                            }
                    )
            )
            @RequestBody @Valid Participante newParticipante) {

        EntityModel<Participante> entityModel = assembler.toModel(service.criar(newParticipante));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    // ===================================================================
    // 4. PUT /participantes/{id} — ATUALIZAR
    // ===================================================================
    @Operation(
            summary = "Atualiza um participante existente",
            description = """
            Atualiza todos os campos de um participante. Se o ID não existir, cria um novo.
            
            **Exemplo de requisição:**
            `PUT /participantes/1`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Participante atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "nome": "João Souza Jr.",
                      "email": "joao.jr@exemplo.com",
                      "telefone": "(11) 99999-4444",
                      "_links": { "self": { "href": "http://localhost:8080/participantes/1" } }
                    }
                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Participante>> updateOrCreateParticipante(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Novos dados do participante",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Participante.class),
                            examples = @ExampleObject(value = """
                        {
                          "nome": "João Souza Jr.",
                          "email": "joao.jr@exemplo.com",
                          "telefone": "(11) 99999-4444"
                        }
                        """)
                    )
            )
            @RequestBody @Valid Participante newParticipante,
            @Parameter(description = "ID do participante a ser atualizado", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(service.atualizarOuCriar(id, newParticipante)));
    }

    // ===================================================================
    // 5. DELETE /participantes/{id} — DELETAR
    // ===================================================================
    @Operation(
            summary = "Deleta um participante",
            description = """
            Remove permanentemente um participante do sistema.
            
            **Atenção:** se o participante possuir inscrições, elas também serão removidas 
            em cascata (por causa do `orphanRemoval = true` em `Participante.inscricoes`).
            
            **Exemplo de requisição:**
            `DELETE /participantes/1`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Participante deletado com sucesso", content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Participante não encontrado",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Não foi possível encontrar o participante com id 999")
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParticipante(
            @Parameter(description = "ID do participante a ser deletado", example = "1", required = true)
            @PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // ===================================================================
    // 6. GET /participantes/buscar — CONSULTA PERSONALIZADA (por nome)
    // ===================================================================
    @Operation(
            summary = "Busca participantes por nome",
            description = """
            Retorna uma lista paginada de participantes cujo nome contenha o termo informado 
            (busca case-insensitive, correspondência parcial).
            
            **Exemplo de requisição:**
            `GET /participantes/buscar?nome=João`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista filtrada retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetro 'nome' não informado", content = @Content)
    })
    @GetMapping("/buscar")
    public ResponseEntity<PagedModel<EntityModel<Participante>>> buscarPorNome(
            @Parameter(description = "Termo a ser buscado no nome", example = "João", required = true)
            @RequestParam String nome,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Participante> page = service.buscarPorNome(nome, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }
}