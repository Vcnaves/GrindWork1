package com.project.grindwork.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Avaliacao {
    private long id;
    private long anuncioId;
    private long usuarioId;
    private String usuarioNome;
    private float nota;
    private String comentario;
    private String dataAvaliacao;
}
