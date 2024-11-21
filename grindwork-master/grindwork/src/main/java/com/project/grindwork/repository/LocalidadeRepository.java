package com.project.grindwork.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.project.grindwork.config.Conexao;
import com.project.grindwork.model.Localidade;

@Repository
public class LocalidadeRepository {

    @Autowired
    private Conexao conexao;

    Connection conn = null;

    public List<Localidade> findAllByOrderByEstadoAsc() {
    Connection conn = conexao.conectaMysql();
    PreparedStatement pstm6 = null;
    ResultSet rs6 = null;
    String sql6 = "";
    List<Localidade> localidades = new ArrayList<>(); 

    try {
        sql6 = "SELECT id, cidade, estado FROM localidade ORDER BY estado ASC";
        pstm6 = conn.prepareStatement(sql6);
        rs6 = pstm6.executeQuery();

        while (rs6.next()) {
            Localidade localidade = new Localidade();
            localidade.setId(rs6.getLong("id"));
            localidade.setCidade(rs6.getString("cidade"));
            localidade.setEstado(rs6.getString("estado"));
            localidades.add(localidade); 
        }
    } catch (Exception e) {
        System.out.println("Erro ao consultar localidades: " + e.getMessage());
    } finally {
        try {
            if (rs6 != null) rs6.close();
            if (pstm6 != null) pstm6.close();
            if (conn != null) conn.close();
        } catch (Exception ex) {
            System.out.println("Erro ao fechar os recursos: " + ex.getMessage());
        }
    }
    return localidades; 
}

}
