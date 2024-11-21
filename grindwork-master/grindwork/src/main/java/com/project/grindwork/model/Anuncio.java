package com.project.grindwork.model;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Anuncio {

  private long id;
  private DadosUsuario usuario;
  private String titulo;
  private String descricao;
  private Double preco;
  private Float nota;
  private String endereco;
  private String dataCriacao;
  private Localidade localidade;
  private ArrayList<Avaliacao> avaliacao;
  private String imageUrl;
}
