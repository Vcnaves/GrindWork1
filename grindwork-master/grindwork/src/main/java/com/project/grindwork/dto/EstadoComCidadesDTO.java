package com.project.grindwork.dto;

import java.util.List;

public class EstadoComCidadesDTO {
    private String estado;
    private List<String> cidades;

    public EstadoComCidadesDTO(String estado, List<String> cidades) {
        this.estado = estado;
        this.cidades = cidades;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<String> getCidades() {
        return cidades;
    }

    public void setCidades(List<String> cidades) {
        this.cidades = cidades;
    }
}
