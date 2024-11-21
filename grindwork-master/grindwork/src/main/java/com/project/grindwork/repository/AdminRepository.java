package com.project.grindwork.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.project.grindwork.config.Conexao;
import com.project.grindwork.exceptions.ObjectInternalErrorServiceException;
import com.project.grindwork.model.Usuario;

@Repository
public class AdminRepository {

    @Autowired
    private Conexao conexao;

    Connection conn = null;

    public ArrayList<Usuario> listarUsuarios() {
        conn = conexao.conectaMysql();
        ArrayList<Usuario> listaUsuarios = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "";

        try {
            sql = " SELECT id, nome, email, status, perfil FROM grindwork.usuario ";
            pstm = conn.prepareStatement(sql);
            rs = pstm.executeQuery();

            while (rs.next()) {
                if (listaUsuarios == null) {
                    listaUsuarios = new ArrayList<>();
                }
                Usuario user = new Usuario();

                user.setId(rs.getLong("id"));
                user.setNome(rs.getString("nome"));
                user.setEmail(rs.getString("email"));
                user.setStatus(rs.getInt("status"));
                user.setPerfil(rs.getInt("perfil"));
                listaUsuarios.add(user);
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar usuários!\n" + e.getMessage());
            throw new ObjectInternalErrorServiceException("Erro ao listar usuários!\n" + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                    pstm.close();
                    rs.close();
                }
                conn = null;
                pstm = null;
                rs = null;
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Erro ao encerrar conexão: " + ex.getMessage());
                throw new ObjectInternalErrorServiceException("Erro ao encerrar conexão!\n" + ex.getMessage());
            }
        }
        return listaUsuarios;
    }

    public void atualizarPerfil(Usuario usuario) {
        conn = conexao.conectaMysql();
        PreparedStatement pstm4 = null;
        String sql4 = "";

        try {
            sql4 = " UPDATE grindwork.usuario SET perfil=? WHERE id=?; ";
            pstm4 = conn.prepareStatement(sql4);
            pstm4.setInt(1, usuario.getPerfil());
            pstm4.setLong(2, usuario.getId());
            pstm4.execute();

        } catch (Exception e) {
            System.out.println("Erro ao alterar perfil: " + e.getMessage());
            throw new ObjectInternalErrorServiceException("Erro ao alterar perfil!\n" + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                    pstm4.close();
                }
                conn = null;
                pstm4 = null;
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Erro ao encerrar conexão: " + ex.getMessage());
                throw new ObjectInternalErrorServiceException("Erro ao encerrar conexão!\n" + ex.getMessage());
            }
        }
    }

    public void atualizarStatusUsuario(Usuario usuario) {
        conn = conexao.conectaMysql();
        PreparedStatement pstm4 = null;
        String sql4 = "";

        try {
            sql4 = " UPDATE grindwork.usuario SET status=? WHERE id=?; ";
            pstm4 = conn.prepareStatement(sql4);
            pstm4.setInt(1, usuario.getStatus());
            pstm4.setLong(2, usuario.getId());
            pstm4.execute();

        } catch (Exception e) {
            System.out.println("Erro ao alterar status: " + e.getMessage());
            throw new ObjectInternalErrorServiceException("Erro ao alterar status!\n" + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                    pstm4.close();
                }
                conn = null;
                pstm4 = null;
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Erro ao encerrar conexão: " + ex.getMessage());
                throw new ObjectInternalErrorServiceException("Erro ao encerrar conexão!\n" + ex.getMessage());
            }
        }
    }

}
