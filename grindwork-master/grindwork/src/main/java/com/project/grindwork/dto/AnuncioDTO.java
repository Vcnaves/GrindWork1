package com.project.grindwork.dto;

import java.io.Serializable;
import java.util.ArrayList;

import com.project.grindwork.model.Anuncio;
import com.project.grindwork.model.Avaliacao;
import com.project.grindwork.model.DadosUsuario;
import com.project.grindwork.model.Localidade;

import lombok.Data;

@Data
public class AnuncioDTO implements Serializable {
    private static final long serialVersionUID = 1L;

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

    public AnuncioDTO() {
    }

    public AnuncioDTO(Anuncio obj) {
        id = obj.getId();
        usuario = obj.getUsuario();
        titulo = obj.getTitulo();
        descricao = obj.getDescricao();
        preco = obj.getPreco();
        nota = obj.getNota();
        endereco = obj.getEndereco();
        localidade = obj.getLocalidade();
        dataCriacao = obj.getDataCriacao();
        avaliacao = obj.getAvaliacao();
        imageUrl = obj.getImageUrl();
        
    }

}
