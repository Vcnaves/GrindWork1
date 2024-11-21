package com.project.grindwork.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.grindwork.dto.EstadoComCidadesDTO;
import com.project.grindwork.service.LocalidadeService;

@RestController
@RequestMapping("/localidades")
public class LocalidadeController {

    @Autowired
    private LocalidadeService localidadeService;

    @GetMapping("/estados-cidades")
    public List<EstadoComCidadesDTO> getEstadosComCidades() {
        return localidadeService.getEstadosComCidades();
    }  
}
