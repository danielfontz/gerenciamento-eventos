package com.danielfontz.gerenciamento_eventos.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.danielfontz.gerenciamento_eventos.controller.ParticipanteController;
import com.danielfontz.gerenciamento_eventos.model.Participante;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ParticipanteModelAssembler implements RepresentationModelAssembler<Participante, EntityModel<Participante>> {

    @Override
    public EntityModel<Participante> toModel(Participante participante) {
        return EntityModel.of(participante,
                linkTo(methodOn(ParticipanteController.class).getParticipanteById(participante.getId())).withSelfRel(),
                linkTo(methodOn(ParticipanteController.class).getAllParticipantes(Pageable.unpaged())).withRel("participantes"),
                linkTo(methodOn(ParticipanteController.class).updateOrCreateParticipante(null, participante.getId())).withRel("update"),
                linkTo(methodOn(ParticipanteController.class).deleteParticipante(participante.getId())).withRel("delete")
        );
    }
}