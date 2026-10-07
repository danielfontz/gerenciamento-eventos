package com.danielfontz.gerenciamento_eventos.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.danielfontz.gerenciamento_eventos.model.Evento;
import com.danielfontz.gerenciamento_eventos.controller.EventoController;

@Component
class EventoModelAssembler implements RepresentationModelAssembler<Evento, EntityModel<Evento>> {

    @Override
    public EntityModel<Evento> toModel(Evento evento) {
        return EntityModel.of(evento,
                linkTo(methodOn(EventoController.class).getEventoById(evento.getId())).withSelfRel(),
                linkTo(methodOn(EventoController.class).getAllEventos(Pageable.unpaged())).withRel("eventos"),
                linkTo(methodOn(EventoController.class).updateOrCreateEvento(evento.getId(), null)).withRel("update"),
                linkTo(methodOn(EventoController.class).deleteEvento(evento.getId())).withRel("delete")
        );
    }
}