package com.project.grindwork.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DadosUsuario extends Usuario{
    
    private String nome;
    private String telefone;
    private Localidade localidade;
    private String endereco;
    private String data_nascimento;
    private ArrayList<Anuncio> anuncio;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DadosUsuario)) return false;
        if (!super.equals(o)) return false;

        DadosUsuario that = (DadosUsuario) o;
        return Objects.equals(anuncio, that.anuncio);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (anuncio != null ? anuncio.hashCode() : 0);
        return result;
    }
}
