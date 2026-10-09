package com.danielfontz.gerenciamento_eventos.controller;

import com.danielfontz.gerenciamento_eventos.assembler.PalestranteModelAssembler;
import com.danielfontz.gerenciamento_eventos.model.Palestrante;
import com.danielfontz.gerenciamento_eventos.service.PalestranteService;
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
 * Controller responsável por todas as operações relacionadas a Palestrantes.
 * Expõe 7 endpoints RESTful documentados com OpenAPI/Swagger.
 */
@RestController
@RequestMapping("/palestrantes")
@Tag(name = "Palestrantes", description = "Operações para gerenciamento completo de palestrantes")
public class PalestranteController {

    private final PalestranteService service;
    private final PalestranteModelAssembler assembler;
    private final PagedResourcesAssembler<Palestrante> pagedResourcesAssembler;

    public PalestranteController(PalestranteService service,
                                 PalestranteModelAssembler assembler,
                                 PagedResourcesAssembler<Palestrante> pagedResourcesAssembler) {
        this.service = service;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    // ===================================================================
    // 1. GET /palestrantes — LISTAR (paginado)
    // ===================================================================
    @Operation(
            summary = "Lista todos os palestrantes (paginado)",
            description = """
            Retorna uma lista paginada de todos os palestrantes cadastrados no sistema.
            
            **Parâmetros de paginação:**
            - `page`: Número da página (começa em 0). Padrão: 0.
            - `size`: Quantidade de itens por página. Padrão: 10.
            - `sort`: Campo de ordenação (ex: `nome`, `especialidade`). Padrão: id, asc.
            
            **Exemplo de requisição:**
            `GET /palestrantes?page=0&size=5&sort=nome,asc`
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
                            "palestranteList": [
                              {
                                "id": 1,
                                "nome": "Ana Silva",
                                "email": "ana@exemplo.com",
                                "especialidade": "Java",
                                "_links": {
                                  "self": { "href": "http://localhost:8080/palestrantes/1" },
                                  "palestrantes": { "href": "http://localhost:8080/palestrantes" },
                                  "update": { "href": "http://localhost:8080/palestrantes/1" },
                                  "delete": { "href": "http://localhost:8080/palestrantes/1" }
                                }
                              },
                              {
                                "id": 2,
                                "nome": "Bruno Costa",
                                "email": "bruno@exemplo.com",
                                "especialidade": "Spring Boot",
                                "_links": {
                                  "self": { "href": "http://localhost:8080/palestrantes/2" },
                                  "palestrantes": { "href": "http://localhost:8080/palestrantes" },
                                  "update": { "href": "http://localhost:8080/palestrantes/2" },
                                  "delete": { "href": "http://localhost:8080/palestrantes/2" }
                                }
                              }
                            ]
                          },
                          "_links": {
                            "self": { "href": "http://localhost:8080/palestrantes?page=0&size=5" },
                            "first": { "href": "http://localhost:8080/palestrantes?page=0&size=5" },
                            "last": { "href": "http://localhost:8080/palestrantes?page=0&size=5" }
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
    public ResponseEntity<PagedModel<EntityModel<Palestrante>>> getAllPalestrantes(
            @ParameterObject
            @PageableDefault(size = 10, page = 0, sort = "id")
            Pageable pageable) {
        Page<Palestrante> page = service.listar(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    // ===================================================================
    // 2. GET /palestrantes/{id} — BUSCAR POR ID
    // ===================================================================
    @Operation(
            summary = "Busca um palestrante pelo ID",
            description = """
            Retorna os detalhes completos de um palestrante específico.
            
            **Observação:** o campo `eventos` é ocultado para evitar referência circular 
            (um palestrante pode estar em vários eventos). Para ver os eventos de um 
            palestrante, consulte `GET /eventos` e filtre pelo nome.
            
            **Exemplo de requisição:**
            `GET /palestrantes/1`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Palestrante encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Palestrante.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de palestrante",
                                    value = """
                        {
                          "id": 1,
                          "nome": "Ana Silva",
                          "email": "ana@exemplo.com",
                          "especialidade": "Java",
                          "_links": {
                            "self": { "href": "http://localhost:8080/palestrantes/1" },
                            "palestrantes": { "href": "http://localhost:8080/palestrantes" },
                            "update": { "href": "http://localhost:8080/palestrantes/1" },
                            "delete": { "href": "http://localhost:8080/palestrantes/1" }
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Palestrante não encontrado com o ID informado",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Não foi possível encontrar o palestrante com id 999")
                    )
            )
    })
    @GetMapping("/{id}")
    public EntityModel<Palestrante> getPalestranteById(
            @Parameter(
                    description = "ID do palestrante a ser buscado",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        return assembler.toModel(service.buscarPorId(id));
    }

    // ===================================================================
    // 3. POST /palestrantes — CRIAR
    // ===================================================================
    @Operation(
            summary = "Cria um novo palestrante",
            description = """
            Cadastra um novo palestrante no sistema.
            
            **Regras de negócio:**
            - `nome`: obrigatório, no máximo 100 caracteres.
            - `email`: obrigatório, formato de e-mail válido, **único** no sistema.
            - `especialidade`: opcional, no máximo 100 caracteres.
            
            **Exemplo de requisição:**
            `POST /palestrantes`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Palestrante criado com sucesso",
                    headers = @Header(
                            name = "Location",
                            description = "URL do palestrante recém-criado",
                            schema = @Schema(type = "string", example = "http://localhost:8080/palestrantes/4")
                    ),
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Palestrante criado",
                                    value = """
                        {
                          "id": 4,
                          "nome": "Diego Lima",
                          "email": "diego@exemplo.com",
                          "especialidade": "Kotlin",
                          "_links": {
                            "self": { "href": "http://localhost:8080/palestrantes/4" },
                            "palestrantes": { "href": "http://localhost:8080/palestrantes" },
                            "update": { "href": "http://localhost:8080/palestrantes/4" },
                            "delete": { "href": "http://localhost:8080/palestrantes/4" }
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
                      "path": "/palestrantes"
                    }
                    """)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<?> createPalestrante(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do palestrante a ser criado",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Palestrante.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Palestrante completo",
                                            summary = "Exemplo com todos os campos",
                                            value = """
                                {
                                  "nome": "Diego Lima",
                                  "email": "diego@exemplo.com",
                                  "especialidade": "Kotlin"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Palestrante mínimo",
                                            summary = "Sem especialidade",
                                            value = """
                                {
                                  "nome": "Eduarda Reis",
                                  "email": "eduarda@exemplo.com"
                                }
                                """
                                    )
                            }
                    )
            )
            @RequestBody @Valid Palestrante newPalestrante) {

        EntityModel<Palestrante> entityModel = assembler.toModel(service.criar(newPalestrante));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    // ===================================================================
    // 4. PUT /palestrantes/{id} — ATUALIZAR
    // ===================================================================
    @Operation(
            summary = "Atualiza um palestrante existente",
            description = """
            Atualiza todos os campos de um palestrante. Se o ID não existir, cria um novo.
            
            **Exemplo de requisição:**
            `PUT /palestrantes/1`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Palestrante atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "nome": "Ana Silva Santos",
                      "email": "ana.santos@exemplo.com",
                      "especialidade": "Java Avançado",
                      "_links": { "self": { "href": "http://localhost:8080/palestrantes/1" } }
                    }
                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Palestrante>> updateOrCreatePalestrante(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Novos dados do palestrante",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Palestrante.class),
                            examples = @ExampleObject(value = """
                        {
                          "nome": "Ana Silva Santos",
                          "email": "ana.santos@exemplo.com",
                          "especialidade": "Java Avançado"
                        }
                        """)
                    )
            )
            @RequestBody @Valid Palestrante newPalestrante,
            @Parameter(description = "ID do palestrante a ser atualizado", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(service.atualizarOuCriar(id, newPalestrante)));
    }

    // ===================================================================
    // 5. DELETE /palestrantes/{id} — DELETAR
    // ===================================================================
    @Operation(
            summary = "Deleta um palestrante",
            description = """
            Remove permanentemente um palestrante do sistema.
            
            **Atenção:** se o palestrante estiver associado a algum evento, a operação 
            pode falhar por violação de integridade referencial.
            
            **Exemplo de requisição:**
            `DELETE /palestrantes/1`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Palestrante deletado com sucesso", content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Palestrante não encontrado",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Não foi possível encontrar o palestrante com id 999")
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePalestrante(
            @Parameter(description = "ID do palestrante a ser deletado", example = "1", required = true)
            @PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // ===================================================================
    // 6. GET /palestrantes/nome — CONSULTA PERSONALIZADA (por nome)
    // ===================================================================
    @Operation(
            summary = "Busca palestrantes por nome",
            description = """
            Retorna uma lista paginada de palestrantes cujo nome contenha o termo informado 
            (busca case-insensitive, correspondência parcial).
            
            **Exemplo de requisição:**
            `GET /palestrantes/nome?nome=Ana`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista filtrada retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetro 'nome' não informado", content = @Content)
    })
    @GetMapping("/nome")
    public ResponseEntity<PagedModel<EntityModel<Palestrante>>> buscarPorNome(
            @Parameter(description = "Termo a ser buscado no nome", example = "Ana", required = true)
            @RequestParam String nome,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Palestrante> page = service.buscarPorNome(nome, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    // ===================================================================
    // 7. GET /palestrantes/especialidade — CONSULTA PERSONALIZADA (por especialidade)
    // ===================================================================
    @Operation(
            summary = "Busca palestrantes por especialidade",
            description = """
            Retorna uma lista paginada de palestrantes filtrados por especialidade 
            (busca case-insensitive, correspondência exata).
            
            **Exemplos de valores:**
            - `Java`
            - `Spring Boot`
            - `Arquitetura de Software`
            - `Kotlin`
            
            **Exemplo de requisição:**
            `GET /palestrantes/especialidade?especialidade=Java`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista filtrada retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetro 'especialidade' não informado", content = @Content)
    })
    @GetMapping("/especialidade")
    public ResponseEntity<PagedModel<EntityModel<Palestrante>>> buscarPorEspecialidade(
            @Parameter(description = "Especialidade do palestrante", example = "Java", required = true)
            @RequestParam String especialidade,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Palestrante> page = service.buscarPorEspecialidade(especialidade, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }
}