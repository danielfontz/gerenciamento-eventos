package com.danielfontz.gerenciamento_eventos.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.danielfontz.gerenciamento_eventos.model.Palestrante;
import com.danielfontz.gerenciamento_eventos.controller.PalestranteController;

@Component
class PalestranteModelAssembler implements RepresentationModelAssembler<Palestrante, EntityModel<Palestrante>> {

    @Override
    public EntityModel<Palestrante> toModel(Palestrante palestrante) {
        return EntityModel.of(palestrante,
                linkTo(methodOn(PalestranteController.class).getPalestranteById(palestrante.getId())).withSelfRel(),
                linkTo(methodOn(PalestranteController.class).getAllPalestrantes(Pageable.unpaged())).withRel("palestrantes"),
                linkTo(methodOn(PalestranteController.class).updateOrCreatePalestrante(palestrante.getId(), null)).withRel("update"),
                linkTo(methodOn(PalestranteController.class).deletePalestrante(palestrante.getId())).withRel("delete")
        );
    }
}