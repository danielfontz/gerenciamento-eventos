package com.danielfontz.gerenciamento_eventos.controller;

import com.danielfontz.gerenciamento_eventos.assembler.PalestranteModelAssembler;
import com.danielfontz.gerenciamento_eventos.exception.ResourceNotFoundException;
import com.danielfontz.gerenciamento_eventos.model.Palestrante;
import com.danielfontz.gerenciamento_eventos.repository.PalestranteRepository;
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
@RequestMapping("/palestrantes")
@Tag(name = "Palestrantes", description = "Gerenciamento de palestrantes")
public class PalestranteController {

    private final PalestranteRepository repository;
    private final PalestranteModelAssembler assembler;
    private final PagedResourcesAssembler<Palestrante> pagedResourcesAssembler;

    public PalestranteController(PalestranteRepository repository,
                                 PalestranteModelAssembler assembler,
                                 PagedResourcesAssembler<Palestrante> pagedResourcesAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Lista todos os palestrantes")
    @ApiResponse(responseCode = "200", description = "Lista paginada de palestrantes")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Palestrante>>> getAllPalestrantes(
            @ParameterObject @PageableDefault(size = 10, page = 0, sort = "id") Pageable pageable) {
        Page<Palestrante> page = repository.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    @Operation(summary = "Busca um palestrante pelo ID")
    @ApiResponse(responseCode = "200", description = "Palestrante encontrado")
    @ApiResponse(responseCode = "404", description = "Palestrante não encontrado", content = @Content)
    @GetMapping("/{id}")
    public EntityModel<Palestrante> getPalestranteById(@PathVariable Long id) {
        Palestrante p = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Palestrante não encontrado com id " + id));
        return assembler.toModel(p);
    }

    @Operation(summary = "Cria um novo palestrante")
    @ApiResponse(responseCode = "201", description = "Palestrante criado")
    @ApiResponse(responseCode = "400", description = "Payload inválido")
    @PostMapping
    public ResponseEntity<?> createPalestrante(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Palestrante.class),
                            examples = @ExampleObject(value = """
                                    { "nome": "Ana Silva", "email": "ana@exemplo.com", "especialidade": "Java" }
                                    """))
            )
            @RequestBody @Valid Palestrante newPalestrante) {

        EntityModel<Palestrante> entityModel = assembler.toModel(repository.save(newPalestrante));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @Operation(summary = "Atualiza ou cria um palestrante pelo ID")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Palestrante>> updateOrCreatePalestrante(
            @RequestBody @Valid Palestrante newPalestrante, @PathVariable Long id) {

        Palestrante updated = repository.findById(id)
                .map(p -> {
                    p.setNome(newPalestrante.getNome());
                    p.setEmail(newPalestrante.getEmail());
                    p.setEspecialidade(newPalestrante.getEspecialidade());
                    return repository.save(p);
                })
                .orElseGet(() -> {
                    newPalestrante.setId(id);
                    return repository.save(newPalestrante);
                });

        return ResponseEntity.ok(assembler.toModel(updated));
    }

    @Operation(summary = "Deleta um palestrante")
    @ApiResponse(responseCode = "204", description = "Palestrante deletado")
    @ApiResponse(responseCode = "404", description = "Palestrante não encontrado", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePalestrante(
            @Parameter(description = "ID do palestrante") @PathVariable Long id) {
        if (repository.findById(id).isEmpty())
            return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca palestrantes por especialidade (consulta personalizada)")
    @GetMapping("/buscar")
    public ResponseEntity<PagedModel<EntityModel<Palestrante>>> buscarPorEspecialidade(
            @RequestParam String especialidade,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Palestrante> page = repository.findByEspecialidadeIgnoreCase(especialidade, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }
}