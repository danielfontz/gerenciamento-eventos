package com.danielfontz.gerenciamento_eventos.controller;

import com.danielfontz.gerenciamento_eventos.assembler.LocalModelAssembler;
import com.danielfontz.gerenciamento_eventos.model.Local;
import com.danielfontz.gerenciamento_eventos.service.LocalService;
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
 * Controller responsável por todas as operações relacionadas a Locais.
 * Expõe 8 endpoints RESTful documentados com OpenAPI/Swagger.
 */
@RestController
@RequestMapping("/locais")
@Tag(name = "Locais", description = "Operações para gerenciamento completo de locais de eventos")
public class LocalController {

    private final LocalService service;
    private final LocalModelAssembler assembler;
    private final PagedResourcesAssembler<Local> pagedResourcesAssembler;

    public LocalController(LocalService service,
                           LocalModelAssembler assembler,
                           PagedResourcesAssembler<Local> pagedResourcesAssembler) {
        this.service = service;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    // ===================================================================
    // 1. GET /locais — LISTAR (paginado)
    // ===================================================================
    @Operation(
            summary = "Lista todos os locais (paginado)",
            description = """
            Retorna uma lista paginada de todos os locais cadastrados no sistema.
            
            **Parâmetros de paginação:**
            - `page`: Número da página (começa em 0). Padrão: 0.
            - `size`: Quantidade de itens por página. Padrão: 10.
            - `sort`: Campo de ordenação (ex: `nome`, `cidade`). Padrão: id, asc.
            
            **Exemplo de requisição:**
            `GET /locais?page=0&size=5&sort=nome,asc`
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
                            "localList": [
                              {
                                "id": 1,
                                "nome": "Centro de Convenções",
                                "endereco": "Av. Paulista, 1000",
                                "cidade": "São Paulo",
                                "_links": {
                                  "self": { "href": "http://localhost:8080/locais/1" },
                                  "locais": { "href": "http://localhost:8080/locais" },
                                  "update": { "href": "http://localhost:8080/locais/1" },
                                  "delete": { "href": "http://localhost:8080/locais/1" }
                                }
                              },
                              {
                                "id": 2,
                                "nome": "Auditório Central",
                                "endereco": "Rua das Flores, 250",
                                "cidade": "Rio de Janeiro",
                                "_links": {
                                  "self": { "href": "http://localhost:8080/locais/2" },
                                  "locais": { "href": "http://localhost:8080/locais" },
                                  "update": { "href": "http://localhost:8080/locais/2" },
                                  "delete": { "href": "http://localhost:8080/locais/2" }
                                }
                              }
                            ]
                          },
                          "_links": {
                            "self": { "href": "http://localhost:8080/locais?page=0&size=5" },
                            "first": { "href": "http://localhost:8080/locais?page=0&size=5" },
                            "last": { "href": "http://localhost:8080/locais?page=0&size=5" }
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
    public ResponseEntity<PagedModel<EntityModel<Local>>> getAllLocais(
            @ParameterObject
            @PageableDefault(size = 10, page = 0, sort = "id")
            Pageable pageable) {
        Page<Local> page = service.listar(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    // ===================================================================
    // 2. GET /locais/{id} — BUSCAR POR ID
    // ===================================================================
    @Operation(
            summary = "Busca um local pelo ID",
            description = """
            Retorna os detalhes completos de um local específico.
            
            **Observação:** o campo `evento` é ocultado para evitar referência circular 
            (um local pode estar associado a um evento). Para ver o evento, use 
            `GET /eventos/{id}`.
            
            **Exemplo de requisição:**
            `GET /locais/1`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Local encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Local.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de local",
                                    value = """
                        {
                          "id": 1,
                          "nome": "Centro de Convenções",
                          "endereco": "Av. Paulista, 1000",
                          "cidade": "São Paulo",
                          "_links": {
                            "self": { "href": "http://localhost:8080/locais/1" },
                            "locais": { "href": "http://localhost:8080/locais" },
                            "update": { "href": "http://localhost:8080/locais/1" },
                            "delete": { "href": "http://localhost:8080/locais/1" }
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Local não encontrado com o ID informado",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Não foi possível encontrar o local com id 999")
                    )
            )
    })
    @GetMapping("/{id}")
    public EntityModel<Local> getLocalById(
            @Parameter(
                    description = "ID do local a ser buscado",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        return assembler.toModel(service.buscarPorId(id));
    }

    // ===================================================================
    // 3. POST /locais — CRIAR
    // ===================================================================
    @Operation(
            summary = "Cria um novo local",
            description = """
            Cadastra um novo local no sistema.
            
            **Regras de negócio:**
            - `nome`: obrigatório, no máximo 100 caracteres, **único** no sistema.
            - `endereco`: obrigatório, no máximo 200 caracteres.
            - `cidade`: obrigatório, no máximo 80 caracteres.
            
            **Exemplo de requisição:**
            `POST /locais`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Local criado com sucesso",
                    headers = @Header(
                            name = "Location",
                            description = "URL do local recém-criado",
                            schema = @Schema(type = "string", example = "http://localhost:8080/locais/4")
                    ),
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Local criado",
                                    value = """
                        {
                          "id": 4,
                          "nome": "Teatro Municipal",
                          "endereco": "Praça Ramos de Azevedo, s/n",
                          "cidade": "São Paulo",
                          "_links": {
                            "self": { "href": "http://localhost:8080/locais/4" },
                            "locais": { "href": "http://localhost:8080/locais" },
                            "update": { "href": "http://localhost:8080/locais/4" },
                            "delete": { "href": "http://localhost:8080/locais/4" }
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload inválido ou nome de local já existente",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-10-09T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "path": "/locais"
                    }
                    """)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<?> createLocal(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do local a ser criado",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Local.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Local completo",
                                            summary = "Exemplo com todos os campos",
                                            value = """
                                {
                                  "nome": "Teatro Municipal",
                                  "endereco": "Praça Ramos de Azevedo, s/n",
                                  "cidade": "São Paulo"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Local no Rio",
                                            summary = "Exemplo em outra cidade",
                                            value = """
                                {
                                  "nome": "Copacabana Palace",
                                  "endereco": "Av. Atlântica, 1702",
                                  "cidade": "Rio de Janeiro"
                                }
                                """
                                    )
                            }
                    )
            )
            @RequestBody @Valid Local newLocal) {

        EntityModel<Local> entityModel = assembler.toModel(service.criar(newLocal));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    // ===================================================================
    // 4. PUT /locais/{id} — ATUALIZAR
    // ===================================================================
    @Operation(
            summary = "Atualiza um local existente",
            description = """
            Atualiza todos os campos de um local. Se o ID não existir, cria um novo.
            
            **Exemplo de requisição:**
            `PUT /locais/1`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Local atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "nome": "Centro de Convenções Paulista",
                      "endereco": "Av. Paulista, 1500",
                      "cidade": "São Paulo",
                      "_links": { "self": { "href": "http://localhost:8080/locais/1" } }
                    }
                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Local>> updateOrCreateLocal(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Novos dados do local",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Local.class),
                            examples = @ExampleObject(value = """
                        {
                          "nome": "Centro de Convenções Paulista",
                          "endereco": "Av. Paulista, 1500",
                          "cidade": "São Paulo"
                        }
                        """)
                    )
            )
            @RequestBody @Valid Local newLocal,
            @Parameter(description = "ID do local a ser atualizado", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(service.atualizarOuCriar(id, newLocal)));
    }

    // ===================================================================
    // 5. DELETE /locais/{id} — DELETAR
    // ===================================================================
    @Operation(
            summary = "Deleta um local",
            description = """
            Remove permanentemente um local do sistema.
            
            **Atenção:** se o local estiver associado a um evento, a operação pode 
            falhar por violação de integridade referencial. Delete o evento primeiro 
            ou altere seu local.
            
            **Exemplo de requisição:**
            `DELETE /locais/1`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Local deletado com sucesso", content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Local não encontrado",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Não foi possível encontrar o local com id 999")
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocal(
            @Parameter(description = "ID do local a ser deletado", example = "1", required = true)
            @PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // ===================================================================
    // 6. GET /locais/buscar — CONSULTA PERSONALIZADA (por cidade)
    // ===================================================================
    @Operation(
            summary = "Busca locais por cidade",
            description = """
            Retorna uma lista paginada de locais que estejam na cidade informada 
            (busca case-insensitive, correspondência exata).
            
            **Exemplo de requisição:**
            `GET /locais/buscar?cidade=São Paulo`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista filtrada retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetro 'cidade' não informado", content = @Content)
    })
    @GetMapping("/buscar")
    public ResponseEntity<PagedModel<EntityModel<Local>>> buscarPorCidade(
            @Parameter(description = "Nome da cidade", example = "São Paulo", required = true)
            @RequestParam String cidade,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Local> page = service.buscarPorCidade(cidade, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    // ===================================================================
    // 7. GET /locais/nome — CONSULTA PERSONALIZADA (por nome)
    // ===================================================================
    @Operation(
            summary = "Busca locais por nome",
            description = """
            Retorna uma lista paginada de locais cujo nome contenha o termo informado 
            (busca case-insensitive, correspondência parcial).
            
            **Exemplo de requisição:**
            `GET /locais/nome?nome=Centro`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista filtrada retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetro 'nome' não informado", content = @Content)
    })
    @GetMapping("/nome")
    public ResponseEntity<PagedModel<EntityModel<Local>>> buscarPorNome(
            @Parameter(description = "Termo a ser buscado no nome do local", example = "Centro", required = true)
            @RequestParam String nome,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Local> page = service.buscarPorNome(nome, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    // ===================================================================
    // 8. GET /locais/endereco — CONSULTA PERSONALIZADA (por endereço)
    // ===================================================================
    @Operation(
            summary = "Busca locais por endereço",
            description = """
            Retorna uma lista paginada de locais cujo endereço contenha o termo informado 
            (busca case-insensitive, correspondência parcial).
            
            **Exemplo de requisição:**
            `GET /locais/endereco?endereco=Paulista`
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista filtrada retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetro 'endereco' não informado", content = @Content)
    })
    @GetMapping("/endereco")
    public ResponseEntity<PagedModel<EntityModel<Local>>> buscarPorEndereco(
            @Parameter(description = "Termo a ser buscado no endereço", example = "Paulista", required = true)
            @RequestParam String endereco,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Local> page = service.buscarPorEndereco(endereco, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }
}