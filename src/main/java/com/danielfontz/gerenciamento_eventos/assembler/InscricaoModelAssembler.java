package com.danielfontz.gerenciamento_eventos.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.danielfontz.gerenciamento_eventos.model.Inscricao;
import com.danielfontz.gerenciamento_eventos.controller.InscricaoController;

@Component
class InscricaoModelAssembler implements RepresentationModelAssembler<Inscricao, EntityModel<Inscricao>> {

    @Override
    public EntityModel<Inscricao> toModel(Inscricao inscricao) {
        return EntityModel.of(inscricao,
                linkTo(methodOn(InscricaoController.class).getInscricaoById(inscricao.getId())).withSelfRel(),
                linkTo(methodOn(InscricaoController.class).getAllInscricoes(Pageable.unpaged())).withRel("inscricoes"),
                linkTo(methodOn(InscricaoController.class).updateOrCreateInscricao(inscricao.getId(), null)).withRel("update"),
                linkTo(methodOn(InscricaoController.class).deleteInscricao(inscricao.getId())).withRel("delete")
        );
    }
}