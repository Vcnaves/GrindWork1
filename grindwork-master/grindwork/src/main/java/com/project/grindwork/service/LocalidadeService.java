package com.project.grindwork.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.grindwork.dto.EstadoComCidadesDTO;
import com.project.grindwork.model.Localidade;
import com.project.grindwork.repository.LocalidadeRepository;

@Service
public class LocalidadeService {
    
    @Autowired
    private LocalidadeRepository localidadeRepository;

    public List<EstadoComCidadesDTO> getEstadosComCidades() {
        List<Localidade> localidades = localidadeRepository.findAllByOrderByEstadoAsc();

        // Agrupar por estado e listar as cidades
        Map<String, List<String>> estadoCidadesMap = localidades.stream()
            .collect(Collectors.groupingBy(Localidade::getEstado, 
                    Collectors.mapping(Localidade::getCidade, Collectors.toList())));

        // Criar a lista de DTOs
        List<EstadoComCidadesDTO> resultado = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : estadoCidadesMap.entrySet()) {
            resultado.add(new EstadoComCidadesDTO(entry.getKey(), entry.getValue()));
        }

        return resultado;
    }
}
