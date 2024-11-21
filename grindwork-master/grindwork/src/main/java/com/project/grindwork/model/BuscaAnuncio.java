package com.project.grindwork.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuscaAnuncio {

  private String descricao;
  private int localidade;
}
