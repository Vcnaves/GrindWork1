package com.project.grindwork.repository;

import com.project.grindwork.config.Conexao;
import com.project.grindwork.exceptions.CustomErrorException;
import com.project.grindwork.model.Anuncio;
import com.project.grindwork.model.DadosUsuario;
import com.project.grindwork.model.ErrorResponse;
import com.project.grindwork.model.Localidade;
import com.project.grindwork.model.Usuario;
import com.project.grindwork.util.Utilidades;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepository {

  @Autowired
  private Conexao conexao;

  Connection conn = null;
  CustomErrorException errorException = null;

  public String validaLogin(String email) {
    conn = conexao.conectaMysql();
    PreparedStatement pstm = null;
    ResultSet rs = null;
    String sql = "";
    String senha = "";

    try {
      sql = " SELECT senha FROM grindwork.usuario WHERE email = ?";
      pstm = conn.prepareStatement(sql);
      pstm.setString(1, email.toLowerCase());
      rs = pstm.executeQuery();

      while (rs.next()) {
        senha = rs.getString("senha");
      }
    } catch (Exception e) {
      System.out.println("Erro ao validar login usuário: " + e.getMessage());
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
      }
    }
    return senha;
  }

  public ArrayList<DadosUsuario> consultaDadosUsuario(String email) {
    ArrayList<DadosUsuario> dadosUsuario = null;
    conn = conexao.conectaMysql();
    PreparedStatement pstm1 = null;
    ResultSet rs1 = null;
    String sql1 = "";

    try {
      sql1 = " SELECT usuario.id AS user_id, " +
          " usuario.nome AS user_nome, " +
          " usuario.email AS user_email, " +
          " usuario.perfil AS user_perfil, " +
          " usuario.telefone AS user_telefone, " +
          " usuario.imagem_url AS user_image_url, " +
          " usuario.endereco AS user_endereco, " +
          " usuario.data_nascimento AS user_data_nascimento, " +
          " usuario.localidade_id AS user_localidade_id, " +
          " local.cidade AS user_localidade_cidade, " +
          " local.estado AS user_localidade_estado " +
          " FROM grindwork.usuario usuario " +
          " LEFT JOIN grindwork.localidade local on local.id = usuario.localidade_id " +
          " WHERE usuario.email = ?";

      pstm1 = conn.prepareStatement(sql1);
      pstm1.setString(1, email.toLowerCase());
      rs1 = pstm1.executeQuery();

      while (rs1.next()) {
        if (dadosUsuario == null) {
          dadosUsuario = new ArrayList<>();
        }
        DadosUsuario user = new DadosUsuario();

        user.setId(rs1.getInt("user_id"));
        user.setNome(Utilidades.capitalizeWords(rs1.getString("user_nome")));
        user.setEmail(rs1.getString("user_email"));
        user.setPerfil(rs1.getInt("user_perfil"));
        user.setTelefone(rs1.getString("user_telefone"));
        user.setImagemUrl(rs1.getString("user_image_url"));
        user.setEndereco(rs1.getString("user_endereco"));
        user.setData_nascimento(rs1.getString("user_data_nascimento"));

        Localidade localidade = new Localidade();
        localidade.setId(rs1.getLong("user_localidade_id"));
        localidade.setCidade(rs1.getString("user_localidade_cidade"));
        localidade.setEstado(rs1.getString("user_localidade_estado"));

        user.setLocalidade(localidade);

        ArrayList<Anuncio> anuncios = new ArrayList<>();
        PreparedStatement pstmAnun = conn.prepareStatement(
            "SELECT anun.id, anun.titulo, anun.descricao, anun.preco, anun.usuario_id, anun.localidade_id, anun.endereco, anun.data_criacao, anun.nota, anun.imagem_url "
                +
                "FROM grindwork.anuncio anun " +
                "WHERE anun.status = 1 and anun.usuario_id = ?; ");

        pstmAnun.setLong(1, rs1.getInt("user_id"));

        ResultSet rsAnun = pstmAnun.executeQuery();
        while (rsAnun.next()) {
          Anuncio anuncio = new Anuncio();
          anuncio.setId(rsAnun.getLong("anun.id"));
          anuncio.setTitulo(rsAnun.getString("anun.titulo"));
          anuncio.setDescricao(rsAnun.getString("anun.descricao"));
          anuncio.setPreco(rsAnun.getDouble("anun.preco"));
          anuncio.setNota(rsAnun.getFloat("anun.nota"));
          anuncio.setEndereco(rsAnun.getString("anun.endereco"));
          anuncio.setDataCriacao(rsAnun.getString("anun.data_criacao"));
          anuncio.setImageUrl(rsAnun.getString("anun.imagem_url"));

          anuncios.add(anuncio);
          user.setAnuncio(anuncios);
        }
        rsAnun.close();
        pstmAnun.close();

        dadosUsuario.add(user);
      }
    } catch (Exception e) {
      System.out.println(
          "Erro ao carregar dados do usuário: " + e.getMessage());
    } finally {
      try {
        if (conn != null) {
          conn.close();
          pstm1.close();
          rs1.close();
        }
        conn = null;
        pstm1 = null;
        rs1 = null;
      } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("Erro ao encerrar conexão: " + ex.getMessage());
      }
    }

    return dadosUsuario;
  }

  public boolean novoUsuario(Usuario usuario) {
    conn = conexao.conectaMysql();
    PreparedStatement pstm2 = null;
    ResultSet rs2 = null;
    String sql2 = "";
    boolean isNew = true;

    try {
      sql2 = " SELECT EMAIL FROM grindwork.usuario WHERE EMAIL = ?";
      pstm2 = conn.prepareStatement(sql2);
      pstm2.setString(1, usuario.getEmail().toLowerCase());
      rs2 = pstm2.executeQuery();

      while (rs2.next()) {
        isNew = false;
      }
    } catch (Exception e) {
      System.out.println("Erro ao validar novo usuário: " + e.getMessage());
    } finally {
      try {
        if (conn != null) {
          conn.close();
          pstm2.close();
          rs2.close();
        }
        conn = null;
        pstm2 = null;
        rs2 = null;
      } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("Erro ao encerrar conexão: " + ex.getMessage());
      }
    }

    return isNew;
  }

  public String cadastraUsuario(DadosUsuario usuario) {
    conn = conexao.conectaMysql();
    PreparedStatement pstm3 = null;
    String sql3 = "";
    String status = "";
    Date dtNascimento = (Date) Utilidades.converterStringToDate(
        usuario.getData_nascimento());

    try {
      sql3 = " INSERT INTO grindwork.usuario " +
          " (nome, email, senha, telefone, localidade_id, endereco, data_nascimento) " +
          " VALUES(?,?,?,?,?,?,?) ";
      pstm3 = conn.prepareStatement(sql3);
      pstm3.setString(1, usuario.getNome().trim().toUpperCase());
      pstm3.setString(2, usuario.getEmail().trim().toLowerCase());
      pstm3.setString(3, usuario.getSenha().trim());
      pstm3.setString(4, usuario.getTelefone());
      pstm3.setLong(5, usuario.getLocalidade().getId());
      pstm3.setString(6, usuario.getEndereco());
      pstm3.setDate(7, dtNascimento);
      pstm3.execute();

      status = "Usuário cadastrado com sucesso!";
    } catch (Exception e) {
      status = "Erro ao cadastrar usuário. \n" + e.getMessage();
      System.out.println("Erro ao cadastrar usuário: " + e.getMessage());
    } finally {
      try {
        if (conn != null) {
          conn.close();
          pstm3.close();
        }
        conn = null;
        pstm3 = null;
      } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("Erro ao encerrar conexão: " + ex.getMessage());
      }
    }

    return status;
  }

  public String excluirUsuario(String email) {
    conn = conexao.conectaMysql();
    PreparedStatement pstm4 = null;
    String sql4 = "";
    String status = "";

    try {
      sql4 = " UPDATE grindwork.usuario " + " SET status = 0 " + " WHERE email=?; ";
      pstm4 = conn.prepareStatement(sql4);
      pstm4.setString(1, email.trim().toLowerCase());
      pstm4.execute();

      status = "Usuário excluido com sucesso!";
    } catch (Exception e) {
      status = "Erro ao excluir usuário";
      System.out.println("Erro ao excluir usuário: " + e.getMessage());
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
        System.out.println("Erro ao excluir conexão: " + ex.getMessage());
      }
    }
    return status;
  }

  public Usuario findByLogin(String email) {
    conn = conexao.conectaMysql();
    PreparedStatement pstm = null;
    ResultSet rs = null;
    String sql = "";
    Usuario usuario = null;

    try {
      sql = " SELECT id, nome, email, senha, perfil FROM grindwork.usuario WHERE email = ?";
      pstm = conn.prepareStatement(sql);
      pstm.setString(1, email.trim().toLowerCase());
      rs = pstm.executeQuery();

      while (rs.next()) {
        usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setNome(Utilidades.capitalizeWords(rs.getString("nome")));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setPerfil(rs.getInt("perfil"));
      }
    } catch (Exception e) {
      errorException = new CustomErrorException(
          new ErrorResponse(
              HttpStatusCode.valueOf(500),
              "Erro ao validar login usuário: " +
                  e.getMessage().replaceAll("\"", "")));
      throw new RuntimeException(errorException);
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
      } catch (Exception exception) {
        exception.printStackTrace();
        errorException = new CustomErrorException(
            new ErrorResponse(
                HttpStatusCode.valueOf(500),
                "Erro ao encerrar conexão: " +
                    exception.getMessage().replaceAll("\"", "")));
        throw new RuntimeException(errorException);
      }
    }
    return usuario;
  }

  public void alterarSenha(Usuario usuario) {
    conn = conexao.conectaMysql();
    PreparedStatement pstm4 = null;
    String sql4 = "";

    try {
      sql4 = " UPDATE grindwork.usuario " +
          " SET senha=? " +
          " WHERE email=?; ";
      pstm4 = conn.prepareStatement(sql4);
      pstm4.setString(1, usuario.getSenha());
      pstm4.setString(2, usuario.getEmail().trim().toLowerCase());
      pstm4.execute();

    } catch (Exception e) {
      System.out.println("Erro ao alterar senha: " + e.getMessage());
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
      }
    }
  }

  public Usuario findById(long id) {
    conn = conexao.conectaMysql();
    PreparedStatement pstm = null;
    ResultSet rs = null;
    String sql = "";
    Usuario usuario = null;

    try {
      sql = " SELECT id, nome, email, senha, perfil FROM grindwork.usuario WHERE id = ?";
      pstm = conn.prepareStatement(sql);
      pstm.setLong(1, id);
      rs = pstm.executeQuery();

      while (rs.next()) {
        usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setNome(Utilidades.capitalizeWords(rs.getString("nome")));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setPerfil(rs.getInt("perfil"));
      }
    } catch (Exception e) {
      errorException = new CustomErrorException(
          new ErrorResponse(
              HttpStatusCode.valueOf(500),
              "Erro ao validar login usuário: " +
                  e.getMessage().replaceAll("\"", "")));
      throw new RuntimeException(errorException);
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
      } catch (Exception exception) {
        exception.printStackTrace();
        errorException = new CustomErrorException(
            new ErrorResponse(
                HttpStatusCode.valueOf(500),
                "Erro ao encerrar conexão: " +
                    exception.getMessage().replaceAll("\"", "")));
        throw new RuntimeException(errorException);
      }
    }
    return usuario;
  }

  public void atuaizaImagemUsuario(Usuario usuario) {
    conn = conexao.conectaMysql();
    PreparedStatement pstm4 = null;
    String sql4 = "";

    try {
      sql4 = " UPDATE grindwork.usuario " +
          " SET imagem_url=? " +
          " WHERE id=?; ";
      pstm4 = conn.prepareStatement(sql4);
      pstm4.setString(1, usuario.getImagemUrl());
      pstm4.setLong(2, usuario.getId());
      pstm4.execute();

    } catch (Exception e) {
      System.out.println("Erro ao atualizar imagem de perfil: " + e.getMessage());
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
      }
    }
  }

  public Long validaLocalidade(String cidade, String estado) {
    conn = conexao.conectaMysql();
    PreparedStatement pstm = null;
    ResultSet rs = null;
    String sql = "";
    Long localidade_id = (long) 0;

    try {
      sql = " SELECT id FROM grindwork.localidade WHERE cidade = ? and estado = ?";
      pstm = conn.prepareStatement(sql);
      pstm.setString(1, cidade);
      pstm.setString(2, estado);
      rs = pstm.executeQuery();

      while (rs.next()) {
        localidade_id = rs.getLong("id");
      }
    } catch (Exception e) {
      System.out.println("Erro ao validar localidade: " + e.getMessage());
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
      }
    }
    return localidade_id;
  }

  public Long cadastraLocalidade(String cidade, String estado) {
    conn = conexao.conectaMysql();
    PreparedStatement pstm3 = null;
    String sql3 = "";
    Long localidade_id = (long) 0;

    try {
      sql3 = " INSERT INTO grindwork.localidade  (cidade, estado) VALUES(?, ?) ";
      pstm3 = conn.prepareStatement(sql3);
      pstm3.setString(1, cidade);
      pstm3.setString(2, estado);
      pstm3.execute();

      localidade_id = validaLocalidade(cidade, estado);

    } catch (Exception e) {
      System.out.println("Erro ao cadastrar localidade: " + e.getMessage());
    } finally {
      try {
        if (conn != null) {
          conn.close();
          pstm3.close();
        }
        conn = null;
        pstm3 = null;
      } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("Erro ao encerrar conexão: " + ex.getMessage());
      }
    }

    return localidade_id;
  }

  public boolean atualizarDadosUsuario(DadosUsuario usuario) {
    boolean atualizado = false;
    PreparedStatement pstm8 = null;
    String sql8 = "";
    Date dtNascimento = (Date) Utilidades.converterStringToDate(
        usuario.getData_nascimento());
    Long localidade_id = validaLocalidade(usuario.getLocalidade().getCidade(), usuario.getLocalidade().getEstado());

    if (localidade_id == 0) {
      localidade_id = cadastraLocalidade(usuario.getLocalidade().getCidade().trim(), usuario.getLocalidade().getEstado().trim());
    }

    try {
      conn = conexao.conectaMysql();
      sql8 = "UPDATE grindwork.usuario " +
          "SET nome=?, email=?, ";
      if (!usuario.getSenha().isEmpty()) {
        sql8 += "senha=?, ";
      }
      sql8 += "telefone=?, localidade_id=?, endereco=?, data_nascimento=? WHERE id=?";
      pstm8 = conn.prepareStatement(sql8);

      int paramIndex = 1;
      pstm8.setString(paramIndex++, usuario.getNome().trim().toUpperCase());
      pstm8.setString(paramIndex++, usuario.getEmail().trim().toLowerCase());
      if (!usuario.getSenha().isEmpty()) {
        pstm8.setString(paramIndex++, usuario.getSenha().trim());
      }
      pstm8.setString(paramIndex++, usuario.getTelefone());
      pstm8.setLong(paramIndex++, localidade_id);
      pstm8.setString(paramIndex++, usuario.getEndereco().trim());
      pstm8.setDate(paramIndex++, dtNascimento);
      pstm8.setLong(paramIndex, usuario.getId());

      pstm8.execute();

      atualizado = true;
    } catch (Exception e) {
      atualizado = false;
      System.out.println("Erro ao atualizar dados do usuário: " + e.getMessage());
    } finally {
      try {
        if (conn != null) {
          conn.close();
          pstm8.close();
        }
        conn = null;
        pstm8 = null;
      } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("Erro ao encerrar conexão: " + ex.getMessage());
      }
    }
    return atualizado;
  }

}
