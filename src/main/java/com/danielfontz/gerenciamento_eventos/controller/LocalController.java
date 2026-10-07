package com.danielfontz.gerenciamento_eventos.controller;

import com.danielfontz.gerenciamento_eventos.assembler.LocalModelAssembler;
import com.danielfontz.gerenciamento_eventos.exception.ResourceNotFoundException;
import com.danielfontz.gerenciamento_eventos.model.Local;
import com.danielfontz.gerenciamento_eventos.repository.LocalRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

@RestController
@RequestMapping("/locais")
@Tag(name = "Locais", description = "Gerenciamento de locais")
public class LocalController {

    private final LocalRepository repository;
    private final LocalModelAssembler assembler;
    private final PagedResourcesAssembler<Local> pagedResourcesAssembler;

    public LocalController(LocalRepository repository,
                           LocalModelAssembler assembler,
                           PagedResourcesAssembler<Local> pagedResourcesAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Lista todos os locais")
    @ApiResponse(responseCode = "200", description = "Lista paginada de locais")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Local>>> getAllLocais(
            @ParameterObject @PageableDefault(size = 10, page = 0, sort = "id") Pageable pageable) {
        Page<Local> page = repository.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    @Operation(summary = "Busca um local pelo ID")
    @ApiResponse(responseCode = "200", description = "Local encontrado")
    @ApiResponse(responseCode = "404", description = "Local não encontrado", content = @Content)
    @GetMapping("/{id}")
    public EntityModel<Local> getLocalById(@PathVariable Long id) {
        Local local = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Local não encontrado com id " + id));
        return assembler.toModel(local);
    }

    @Operation(summary = "Cria um novo local")
    @ApiResponse(responseCode = "201", description = "Local criado")
    @ApiResponse(responseCode = "400", description = "Payload inválido")
    @PostMapping
    public ResponseEntity<?> createLocal(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Local.class),
                            examples = @ExampleObject(value = """
                                    { "nome": "Centro de Convenções", "endereco": "Av. Paulista, 1000", "cidade": "São Paulo" }
                                    """))
            )
            @RequestBody @Valid Local newLocal) {

        EntityModel<Local> entityModel = assembler.toModel(repository.save(newLocal));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @Operation(summary = "Atualiza ou cria um local pelo ID")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Local>> updateOrCreateLocal(
            @RequestBody @Valid Local newLocal, @PathVariable Long id) {

        Local updated = repository.findById(id)
                .map(local -> {
                    local.setNome(newLocal.getNome());
                    local.setEndereco(newLocal.getEndereco());
                    local.setCidade(newLocal.getCidade());
                    return repository.save(local);
                })
                .orElseGet(() -> {
                    newLocal.setId(id);
                    return repository.save(newLocal);
                });

        return ResponseEntity.ok(assembler.toModel(updated));
    }

    @Operation(summary = "Deleta um local")
    @ApiResponse(responseCode = "204", description = "Local deletado")
    @ApiResponse(responseCode = "404", description = "Local não encontrado", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocal(
            @Parameter(description = "ID do local") @PathVariable Long id) {
        if (repository.findById(id).isEmpty())
            return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca locais por cidade (consulta personalizada)")
    @GetMapping("/buscar")
    public ResponseEntity<PagedModel<EntityModel<Local>>> buscarPorCidade(
            @RequestParam String cidade,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Local> page = repository.findByCidadeIgnoreCase(cidade, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }
}