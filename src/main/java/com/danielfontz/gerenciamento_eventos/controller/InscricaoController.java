package com.danielfontz.gerenciamento_eventos.controller;

import com.danielfontz.gerenciamento_eventos.assembler.InscricaoModelAssembler;
import com.danielfontz.gerenciamento_eventos.exception.ResourceNotFoundException;
import com.danielfontz.gerenciamento_eventos.model.Inscricao;
import com.danielfontz.gerenciamento_eventos.repository.InscricaoRepository;
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
@RequestMapping("/inscricoes")
@Tag(name = "Inscrições", description = "Gerenciamento de inscrições")
public class InscricaoController {

    private final InscricaoRepository repository;
    private final InscricaoModelAssembler assembler;
    private final PagedResourcesAssembler<Inscricao> pagedResourcesAssembler;

    public InscricaoController(InscricaoRepository repository,
                               InscricaoModelAssembler assembler,
                               PagedResourcesAssembler<Inscricao> pagedResourcesAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Lista todas as inscrições")
    @ApiResponse(responseCode = "200", description = "Lista paginada de inscrições")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Inscricao>>> getAllInscricoes(
            @ParameterObject @PageableDefault(size = 10, page = 0, sort = "id") Pageable pageable) {
        Page<Inscricao> page = repository.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    @Operation(summary = "Busca uma inscrição pelo ID")
    @ApiResponse(responseCode = "200", description = "Inscrição encontrada")
    @ApiResponse(responseCode = "404", description = "Inscrição não encontrada", content = @Content)
    @GetMapping("/{id}")
    public EntityModel<Inscricao> getInscricaoById(@PathVariable Long id) {
        Inscricao i = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscrição não encontrada com id " + id));
        return assembler.toModel(i);
    }

    @Operation(summary = "Cria uma nova inscrição")
    @ApiResponse(responseCode = "201", description = "Inscrição criada")
    @ApiResponse(responseCode = "400", description = "Payload inválido")
    @PostMapping
    public ResponseEntity<?> createInscricao(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Inscricao.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "dataInscricao": "2026-11-01T10:00:00",
                                      "status": "ATIVA",
                                      "evento": { "id": 1 },
                                      "participante": { "id": 1 }
                                    }
                                    """))
            )
            @RequestBody @Valid Inscricao newInscricao) {

        EntityModel<Inscricao> entityModel = assembler.toModel(repository.save(newInscricao));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @Operation(summary = "Atualiza ou cria uma inscrição pelo ID")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Inscricao>> updateOrCreateInscricao(
            @RequestBody @Valid Inscricao newInscricao, @PathVariable Long id) {

        Inscricao updated = repository.findById(id)
                .map(i -> {
                    i.setDataInscricao(newInscricao.getDataInscricao());
                    i.setStatus(newInscricao.getStatus());
                    i.setEvento(newInscricao.getEvento());
                    i.setParticipante(newInscricao.getParticipante());
                    return repository.save(i);
                })
                .orElseGet(() -> {
                    newInscricao.setId(id);
                    return repository.save(newInscricao);
                });

        return ResponseEntity.ok(assembler.toModel(updated));
    }

    @Operation(summary = "Deleta uma inscrição")
    @ApiResponse(responseCode = "204", description = "Inscrição deletada")
    @ApiResponse(responseCode = "404", description = "Inscrição não encontrada", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInscricao(
            @Parameter(description = "ID da inscrição") @PathVariable Long id) {
        if (repository.findById(id).isEmpty())
            return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca inscrições por status (consulta personalizada)")
    @GetMapping("/buscar")
    public ResponseEntity<PagedModel<EntityModel<Inscricao>>> buscarPorStatus(
            @RequestParam String status,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Inscricao> page = repository.findByStatusIgnoreCase(status, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    @Operation(summary = "Busca inscrições por evento (consulta personalizada)")
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<PagedModel<EntityModel<Inscricao>>> buscarPorEvento(
            @PathVariable Long eventoId,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Inscricao> page = repository.findByEventoId(eventoId, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }
}