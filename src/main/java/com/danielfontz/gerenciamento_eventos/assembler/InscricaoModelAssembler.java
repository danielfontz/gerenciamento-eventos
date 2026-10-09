package com.danielfontz.gerenciamento_eventos.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.danielfontz.gerenciamento_eventos.controller.EventoController;
import com.danielfontz.gerenciamento_eventos.controller.InscricaoController;
import com.danielfontz.gerenciamento_eventos.controller.ParticipanteController;
import com.danielfontz.gerenciamento_eventos.model.Inscricao;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class InscricaoModelAssembler implements RepresentationModelAssembler<Inscricao, EntityModel<Inscricao>> {

    @Override
    public EntityModel<Inscricao> toModel(Inscricao inscricao) {
        EntityModel<Inscricao> model = EntityModel.of(inscricao,
                linkTo(methodOn(InscricaoController.class).getInscricaoById(inscricao.getId())).withSelfRel(),
                linkTo(methodOn(InscricaoController.class).getAllInscricoes(Pageable.unpaged())).withRel("inscricoes"),
                linkTo(methodOn(InscricaoController.class).updateOrCreateInscricao(null, inscricao.getId())).withRel("update"),
                linkTo(methodOn(InscricaoController.class).deleteInscricao(inscricao.getId())).withRel("delete")
        );

        // Links de navegação para os recursos associados (HATEOAS)
        if (inscricao.getEvento() != null) {
            model.add(linkTo(methodOn(EventoController.class)
                    .getEventoById(inscricao.getEvento().getId())).withRel("evento"));
        }
        if (inscricao.getParticipante() != null) {
            model.add(linkTo(methodOn(ParticipanteController.class)
                    .getParticipanteById(inscricao.getParticipante().getId())).withRel("participante"));
        }

        return model;
    }
}