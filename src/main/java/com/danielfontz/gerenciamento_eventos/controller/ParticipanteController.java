package com.danielfontz.gerenciamento_eventos.controller;

import com.danielfontz.gerenciamento_eventos.assembler.ParticipanteModelAssembler;
import com.danielfontz.gerenciamento_eventos.exception.ResourceNotFoundException;
import com.danielfontz.gerenciamento_eventos.model.Participante;
import com.danielfontz.gerenciamento_eventos.repository.ParticipanteRepository;
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
@RequestMapping("/participantes")
@Tag(name = "Participantes", description = "Gerenciamento de participantes")
public class ParticipanteController {

    private final ParticipanteRepository repository;
    private final ParticipanteModelAssembler assembler;
    private final PagedResourcesAssembler<Participante> pagedResourcesAssembler;

    public ParticipanteController(ParticipanteRepository repository,
                                  ParticipanteModelAssembler assembler,
                                  PagedResourcesAssembler<Participante> pagedResourcesAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Lista todos os participantes")
    @ApiResponse(responseCode = "200", description = "Lista paginada de participantes")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Participante>>> getAllParticipantes(
            @ParameterObject @PageableDefault(size = 10, page = 0, sort = "id") Pageable pageable) {
        Page<Participante> page = repository.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    @Operation(summary = "Busca um participante pelo ID")
    @ApiResponse(responseCode = "200", description = "Participante encontrado")
    @ApiResponse(responseCode = "404", description = "Participante não encontrado", content = @Content)
    @GetMapping("/{id}")
    public EntityModel<Participante> getParticipanteById(@PathVariable Long id) {
        Participante p = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Participante não encontrado com id " + id));
        return assembler.toModel(p);
    }

    @Operation(summary = "Cria um novo participante")
    @ApiResponse(responseCode = "201", description = "Participante criado")
    @ApiResponse(responseCode = "400", description = "Payload inválido")
    @PostMapping
    public ResponseEntity<?> createParticipante(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Participante.class),
                            examples = @ExampleObject(value = """
                                    { "nome": "João Souza", "email": "joao@exemplo.com", "telefone": "(11) 99999-0000" }
                                    """))
            )
            @RequestBody @Valid Participante newParticipante) {

        EntityModel<Participante> entityModel = assembler.toModel(repository.save(newParticipante));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @Operation(summary = "Atualiza ou cria um participante pelo ID")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Participante>> updateOrCreateParticipante(
            @RequestBody @Valid Participante newParticipante, @PathVariable Long id) {

        Participante updated = repository.findById(id)
                .map(p -> {
                    p.setNome(newParticipante.getNome());
                    p.setEmail(newParticipante.getEmail());
                    p.setTelefone(newParticipante.getTelefone());
                    return repository.save(p);
                })
                .orElseGet(() -> {
                    newParticipante.setId(id);
                    return repository.save(newParticipante);
                });

        return ResponseEntity.ok(assembler.toModel(updated));
    }

    @Operation(summary = "Deleta um participante")
    @ApiResponse(responseCode = "204", description = "Participante deletado")
    @ApiResponse(responseCode = "404", description = "Participante não encontrado", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParticipante(
            @Parameter(description = "ID do participante") @PathVariable Long id) {
        if (repository.findById(id).isEmpty())
            return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca participantes por nome (consulta personalizada)")
    @GetMapping("/buscar")
    public ResponseEntity<PagedModel<EntityModel<Participante>>> buscarPorNome(
            @RequestParam String nome,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Participante> page = repository.findByNomeContainingIgnoreCase(nome, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }
}