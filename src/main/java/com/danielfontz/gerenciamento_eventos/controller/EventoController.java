package com.danielfontz.gerenciamento_eventos.controller;

import com.danielfontz.gerenciamento_eventos.assembler.EventoModelAssembler;
import com.danielfontz.gerenciamento_eventos.exception.ResourceNotFoundException;
import com.danielfontz.gerenciamento_eventos.model.Evento;
import com.danielfontz.gerenciamento_eventos.repository.EventoRepository;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/eventos")
@Tag(name = "Eventos", description = "Gerenciamento de eventos")
public class EventoController {

    private final EventoRepository repository;
    private final EventoModelAssembler assembler;
    private final PagedResourcesAssembler<Evento> pagedResourcesAssembler;

    public EventoController(EventoRepository repository,
                            EventoModelAssembler assembler,
                            PagedResourcesAssembler<Evento> pagedResourcesAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Lista todos os eventos")
    @ApiResponse(responseCode = "200", description = "Lista paginada de eventos retornada com sucesso")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Evento>>> getAllEventos(
            @ParameterObject @PageableDefault(size = 10, page = 0, sort = "id") Pageable pageable) {
        Page<Evento> page = repository.findAll(pageable);
        PagedModel<EntityModel<Evento>> pagedModel = pagedResourcesAssembler.toModel(page, assembler);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Busca um evento pelo ID")
    @ApiResponse(responseCode = "200", description = "Evento encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Evento.class)))
    @ApiResponse(responseCode = "404", description = "Evento não encontrado", content = @Content)
    @GetMapping("/{id}")
    public EntityModel<Evento> getEventoById(@PathVariable Long id) {
        Evento evento = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com id " + id));
        return assembler.toModel(evento);
    }

    @Operation(summary = "Cria um novo evento")
    @ApiResponse(responseCode = "201", description = "Evento criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Payload inválido")
    @PostMapping
    public ResponseEntity<?> createEvento(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Novo evento",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Evento.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "titulo": "Workshop de Spring Boot",
                                      "descricao": "Workshop prático",
                                      "data": "2026-12-15",
                                      "status": "PLANEJADO"
                                    }
                                    """))
            )
            @RequestBody @Valid Evento newEvento) {

        EntityModel<Evento> entityModel = assembler.toModel(repository.save(newEvento));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @Operation(summary = "Atualiza ou cria um evento pelo ID")
    @ApiResponse(responseCode = "200", description = "Evento atualizado")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Evento>> updateOrCreateEvento(
            @RequestBody @Valid Evento newEvento, @PathVariable Long id) {

        Evento updated = repository.findById(id)
                .map(evento -> {
                    evento.setTitulo(newEvento.getTitulo());
                    evento.setDescricao(newEvento.getDescricao());
                    evento.setData(newEvento.getData());
                    evento.setStatus(newEvento.getStatus());
                    evento.setLocal(newEvento.getLocal());
                    return repository.save(evento);
                })
                .orElseGet(() -> {
                    newEvento.setId(id);
                    return repository.save(newEvento);
                });

        return ResponseEntity.ok(assembler.toModel(updated));
    }

    @Operation(summary = "Deleta um evento")
    @ApiResponse(responseCode = "204", description = "Evento deletado com sucesso", content = @Content)
    @ApiResponse(responseCode = "404", description = "Evento não encontrado", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvento(
            @Parameter(description = "ID do evento") @PathVariable Long id) {
        if (repository.findById(id).isEmpty())
            return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca eventos pelo título (consulta personalizada)")
    @ApiResponse(responseCode = "200", description = "Lista paginada filtrada por título")
    @GetMapping("/buscar")
    public ResponseEntity<PagedModel<EntityModel<Evento>>> buscarPorTitulo(
            @RequestParam String titulo,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Evento> page = repository.findByTituloContainingIgnoreCase(titulo, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    @Operation(summary = "Busca eventos por status (consulta personalizada)")
    @ApiResponse(responseCode = "200", description = "Lista paginada filtrada por status")
    @GetMapping("/status/{status}")
    public ResponseEntity<PagedModel<EntityModel<Evento>>> buscarPorStatus(
            @PathVariable com.danielfontz.gerenciamento_eventos.model.StatusEvento status,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Evento> page = repository.findByStatus(status, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }
}