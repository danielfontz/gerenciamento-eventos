package com.danielfontz.gerenciamento_eventos.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.danielfontz.gerenciamento_eventos.controller.LocalController;
import com.danielfontz.gerenciamento_eventos.model.Local;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class LocalModelAssembler implements RepresentationModelAssembler<Local, EntityModel<Local>> {

    @Override
    public EntityModel<Local> toModel(Local local) {
        return EntityModel.of(local,
                linkTo(methodOn(LocalController.class).getLocalById(local.getId())).withSelfRel(),
                linkTo(methodOn(LocalController.class).getAllLocais(Pageable.unpaged())).withRel("locais"),
                linkTo(methodOn(LocalController.class).updateOrCreateLocal(null, local.getId())).withRel("update"),
                linkTo(methodOn(LocalController.class).deleteLocal(local.getId())).withRel("delete")
        );
    }
}