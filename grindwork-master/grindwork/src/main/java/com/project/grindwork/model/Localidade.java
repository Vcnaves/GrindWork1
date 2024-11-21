package com.project.grindwork.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Localidade {
    private long id;
    private String cidade;
    private String estado;
}
