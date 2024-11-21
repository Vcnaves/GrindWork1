package com.project.grindwork.repository;

import com.project.grindwork.config.Conexao;
import com.project.grindwork.exceptions.ObjectInternalErrorServiceException;
import com.project.grindwork.model.Anuncio;
import com.project.grindwork.model.Avaliacao;
import com.project.grindwork.model.DadosUsuario;
import com.project.grindwork.model.Localidade;
import com.project.grindwork.util.Utilidades;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class AnuncioRepository {

  @Autowired
  private UsuarioRepository localidade;

  @Autowired
  private Conexao conexao;

  Connection conn = null;

  public String cadastrarAnuncio(Anuncio anuncio) {
    String statusAnuncio = "";
    PreparedStatement pstm5 = null;
    String sql5 = "";
    Long localidade_id = localidade.validaLocalidade(anuncio.getLocalidade().getCidade(),
        anuncio.getLocalidade().getEstado());

    if (localidade_id == 0) {
      localidade_id = localidade.cadastraLocalidade(anuncio.getLocalidade().getCidade(),
          anuncio.getLocalidade().getEstado());
    }

    try {
      conn = conexao.conectaMysql();
      sql5 = " INSERT INTO grindwork.anuncio " +
          " (titulo, descricao, preco, usuario_id, localidade_id, endereco, data_criacao, status, imagem_url) " +
          " VALUES(?,?,?,?,?,?,SYSDATE(),1,?) ";
      pstm5 = conn.prepareStatement(sql5);
      pstm5.setString(
          1,
          Utilidades.capitalizeWords(anuncio.getTitulo().trim()));
      pstm5.setString(2, anuncio.getDescricao().trim());
      pstm5.setDouble(3, anuncio.getPreco());
      pstm5.setLong(4, anuncio.getUsuario().getId());
      pstm5.setLong(5, localidade_id);
      pstm5.setString(6, anuncio.getEndereco());
      pstm5.setString(7, anuncio.getImageUrl());
      pstm5.execute();

      statusAnuncio = "Anúncio cadastrado com sucesso!";
    } catch (Exception e) {
      statusAnuncio = "Erro ao cadastrar anúncio";
      System.out.println("Erro ao cadastrar anúncio: " + e.getMessage());
      throw new ObjectInternalErrorServiceException("Erro ao cadastrar anúncio! \n" + e.getMessage());
    } finally {
      try {
        if (conn != null) {
          conn.close();
          pstm5.close();
        }
        conn = null;
        pstm5 = null;
      } catch (Exception ex) {
        System.out.println("Erro ao encerrar conexão: " + ex.getMessage());
        throw new ObjectInternalErrorServiceException("Erro ao encerrar conexão! \n" + ex.getMessage());
      }
    }

    return statusAnuncio;
  }

  public String atualizarAnuncio(Anuncio anuncio) {
    String statusAnuncio = "";
    PreparedStatement pstm5 = null;
    String sql5 = "";
    Long localidade_id = localidade.validaLocalidade(anuncio.getLocalidade().getCidade(),
        anuncio.getLocalidade().getEstado());

    if (localidade_id == 0) {
      localidade_id = localidade.cadastraLocalidade(anuncio.getLocalidade().getCidade(),
          anuncio.getLocalidade().getEstado());
    }

    try {
      conn = conexao.conectaMysql();
      StringBuilder sqlBuilder = new StringBuilder(
          "UPDATE grindwork.anuncio SET titulo=?, descricao=?, preco=?, localidade_id=?, endereco=?");

      if (anuncio.getImageUrl() != null && !anuncio.getImageUrl().trim().isEmpty()) {
        sqlBuilder.append(", imagem_url=?");
      }

      sqlBuilder.append(" WHERE id=?");
      sql5 = sqlBuilder.toString();

      pstm5 = conn.prepareStatement(sql5);
      pstm5.setString(1, Utilidades.capitalizeWords(anuncio.getTitulo().trim()));
      pstm5.setString(2, anuncio.getDescricao().trim());
      pstm5.setDouble(3, anuncio.getPreco());
      pstm5.setLong(4, localidade_id);
      pstm5.setString(5, anuncio.getEndereco());

      int paramIndex = 6;
      if (anuncio.getImageUrl() != null && !anuncio.getImageUrl().trim().isEmpty()) {
        pstm5.setString(paramIndex++, anuncio.getImageUrl());
      }

      pstm5.setLong(paramIndex, anuncio.getId());
      pstm5.execute();

      statusAnuncio = "Anúncio atualizado com sucesso!";
    } catch (Exception e) {
      statusAnuncio = "Erro ao atualizar anúncio";
      System.out.println("Erro ao atualizar anúncio: " + e.getMessage());
      throw new ObjectInternalErrorServiceException("Erro ao atualizar anúncio! \n" + e.getMessage());
    } finally {
      try {
        if (conn != null) {
          conn.close();
          pstm5.close();
        }
        conn = null;
        pstm5 = null;
      } catch (Exception ex) {
        System.out.println("Erro ao encerrar conexão: " + ex.getMessage());
        throw new ObjectInternalErrorServiceException("Erro ao encerrar conexão! \n" + ex.getMessage());
      }
    }

    return statusAnuncio;
  }

  public boolean validaLocalidade(long localidade_id) {
    conn = conexao.conectaMysql();
    PreparedStatement pstm6 = null;
    ResultSet rs6 = null;
    String sql6 = "";
    boolean local = false;

    try {
      sql6 = " SELECT cidade FROM grindwork.localidade WHERE id = ?";
      pstm6 = conn.prepareStatement(sql6);
      pstm6.setLong(1, localidade_id);
      rs6 = pstm6.executeQuery();

      while (rs6.next()) {
        local = true;
      }
    } catch (Exception e) {
      System.out.println("Erro ao validar localidade: " + e.getMessage());
      throw new ObjectInternalErrorServiceException("Erro ao validar localidade! \n" + e.getMessage());
    } finally {
      try {
        if (conn != null) {
          conn.close();
          pstm6.close();
          rs6.close();
        }
        conn = null;
        pstm6 = null;
        rs6 = null;
      } catch (Exception ex) {
        System.out.println("Erro ao encerrar conexão: " + ex.getMessage());
        throw new ObjectInternalErrorServiceException("Erro ao encerrar conexãoo! \n" + ex.getMessage());
      }
    }
    return local;
  }

  public ArrayList<Anuncio> listarAnuncio(long localidade_id) {
    ArrayList<Anuncio> anuncios = null;
    conn = conexao.conectaMysql();
    PreparedStatement pstm7 = null;
    ResultSet rs7 = null;
    String sql7 = "";

    try {
      sql7 = " SELECT id, titulo, descricao, preco, usuario_id, localidade_id, endereco, data_criacao " +
          " FROM grindwork.anuncio WHERE status = 1 AND localidade_id = ?;";
      pstm7 = conn.prepareStatement(sql7);
      pstm7.setLong(1, localidade_id);
      rs7 = pstm7.executeQuery();

      while (rs7.next()) {
        anuncios = new ArrayList<>();
        Anuncio anuncio = new Anuncio();

        anuncio.setId(rs7.getLong("id"));
        anuncio.setTitulo(rs7.getString("titulo"));
        anuncio.setDescricao(rs7.getString("descricao"));
        anuncio.setPreco(rs7.getDouble("preco"));
        // anuncio.setUsuario_id(rs7.getLong("usuario_id"));
        anuncio.setEndereco(rs7.getString("endereco"));
        anuncio.setDataCriacao(rs7.getString("data_criacao"));

        Localidade local = new Localidade();
        local.setId(rs7.getLong(" anun.localidade_id"));
        local.setCidade(rs7.getString("local.cidade"));
        local.setEstado(rs7.getString("local.estado"));

        anuncio.setLocalidade(local);
        anuncios.add(anuncio);
      }
    } catch (Exception e) {
      System.out.println("Erro ao validar localidade: " + e.getMessage());
      throw new ObjectInternalErrorServiceException("Erro ao validar localidade! \n" + e.getMessage());
    } finally {
      try {
        if (conn != null) {
          conn.close();
          pstm7.close();
          rs7.close();
        }
        conn = null;
        pstm7 = null;
        rs7 = null;
      } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("Erro ao encerrar conexão: " + ex.getMessage());
        throw new ObjectInternalErrorServiceException("Erro ao encerrar conexão! \n" + ex.getMessage());
      }
    }
    return anuncios;
  }

  public String excluirAnuncio(long id) {
    conn = conexao.conectaMysql();
    PreparedStatement pstm8 = null;
    String sql8 = "";
    String status = "";

    try {
      sql8 = " UPDATE grindwork.anuncio SET status = 0 WHERE id=? ";
      pstm8 = conn.prepareStatement(sql8);
      pstm8.setLong(1, id);
      pstm8.execute();

      status = "Anúncio excluido com sucesso!";
    } catch (Exception e) {
      status = "Erro ao excluir anúncio";
      System.out.println("Erro ao excluir anúncio: " + e.getMessage());
      throw new ObjectInternalErrorServiceException("Erro ao excluir anúncio! \n" + e.getMessage());
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
        throw new ObjectInternalErrorServiceException("Erro ao encerrar conexão! \n" + ex.getMessage());
      }
    }
    return status;
  }

  public String inserirAvaliacao(Avaliacao avaliacao) {
    conn = conexao.conectaMysql();
    PreparedStatement pstm12 = null;
    String sql12 = "";
    String status = "";

    try {
      sql12 = " INSERT INTO grindwork.avaliacao " +
          "( anuncio_id, usuario_id, nota, comentario, data_avaliacao) " +
          "VALUES(?, ?, ?, ?, CURRENT_TIMESTAMP) ";
      pstm12 = conn.prepareStatement(sql12);
      pstm12.setLong(1, avaliacao.getAnuncioId());
      pstm12.setLong(2, avaliacao.getUsuarioId());
      pstm12.setFloat(3, avaliacao.getNota());
      pstm12.setString(4, avaliacao.getComentario().trim());
      pstm12.execute();

      status = "Serviço avaliado com sucesso!";
    } catch (Exception e) {
      status = "Erro ao gravar avaliação: " + e.getMessage();
      System.out.println("Erro ao gravar avaliação: " + e.getMessage());
      throw new ObjectInternalErrorServiceException("Erro ao gravar avaliação! \n" + e.getMessage());
    } finally {
      try {
        if (conn != null) {
          conn.close();
          pstm12.close();
        }
        conn = null;
        pstm12 = null;
      } catch (Exception ex) {
        System.out.println("Erro ao encerrar conexão: " + ex.getMessage());
        throw new ObjectInternalErrorServiceException("Erro ao encerrar conexão! \n" + ex.getMessage());
      }
    }
    return status;
  }

  public String alterarAvaliacao(Avaliacao avaliacao) {
    conn = conexao.conectaMysql();
    PreparedStatement pstm12 = null;
    String sql12 = "";
    String status = "";

    try {
      sql12 = " UPDATE grindwork.avaliacao" +
          " SET nota=?, comentario=?, data_avaliacao=CURRENT_TIMESTAMP " +
          " WHERE anuncio_id= ? and usuario_id = ? ";
      pstm12 = conn.prepareStatement(sql12);
      pstm12.setFloat(1, avaliacao.getNota());
      pstm12.setString(2, avaliacao.getComentario().trim());
      pstm12.setLong(3, avaliacao.getAnuncioId());
      pstm12.setLong(4, avaliacao.getUsuarioId());
      pstm12.execute();

      status = "Avaliação alterada com sucesso!";
    } catch (Exception e) {
      status = "Erro ao alterar avaliação: " + e.getMessage();
      System.out.println("Erro ao alterar avaliação: " + e.getMessage());
    } finally {
      try {
        if (conn != null) {
          conn.close();
          pstm12.close();
        }
        conn = null;
        pstm12 = null;
      } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("Erro ao encerrar conexão: " + ex.getMessage());
      }
    }
    return status;
  }

  public ArrayList<Avaliacao> listarAvaliacoes(Long anuncio_id) {
    ArrayList<Avaliacao> avaliacoes = null;
    conn = conexao.conectaMysql();
    PreparedStatement pstm13 = null;
    ResultSet rs13 = null;
    String sql13 = "";

    try {
      sql13 = " SELECT id, anuncio_id, usuario_id, nota, comentario, data_avaliacao " +
          " FROM grindwork.avaliacao WHERE anuncio_id=? ";
      pstm13 = conn.prepareStatement(sql13);
      pstm13.setLong(1, anuncio_id);
      rs13 = pstm13.executeQuery();

      while (rs13.next()) {
        avaliacoes = new ArrayList<>();
        Avaliacao avaliacao = new Avaliacao();

        avaliacao.setId(rs13.getLong("id"));
        avaliacao.setAnuncioId(rs13.getLong("anuncio_id"));
        avaliacao.setUsuarioId(rs13.getLong("usuario_id"));
        avaliacao.setNota(rs13.getFloat("nota"));
        avaliacao.setComentario(rs13.getString("comentario"));
        avaliacao.setDataAvaliacao(rs13.getString("data_avaliacao"));
        avaliacoes.add(avaliacao);
      }
    } catch (Exception e) {
      System.out.println("Erro ao listar avaliações: " + e.getMessage());
      throw new ObjectInternalErrorServiceException("Erro ao listar avaliações!\n"+ e.getMessage());
    } finally {
      try {
        if (conn != null) {
          conn.close();
          pstm13.close();
          rs13.close();
        }
        conn = null;
        pstm13 = null;
        rs13 = null;
      } catch (Exception ex) {
        System.out.println("Erro ao encerrar conexão: " + ex.getMessage());
        throw new ObjectInternalErrorServiceException("Erro ao encerrar conexão!\n"+ ex.getMessage());
      }
    }
    return avaliacoes;
  }

  public Page<Anuncio> findAll(PageRequest pageRequest, String servico, String localidade) {
    List<Anuncio> anuncios = new ArrayList<>();
    Connection conn = null;
    PreparedStatement pstm7 = null;
    ResultSet rs7 = null;
    try {
      conn = conexao.conectaMysql();

      StringBuilder sql7 = new StringBuilder(
          "SELECT anun.id, anun.titulo, anun.descricao, anun.preco, anun.nota, anun.usuario_id, user.nome, user.email, user.telefone, local.id, anun.endereco, anun.data_criacao, anun.imagem_url, local.cidade, local.estado "
              +
              "FROM grindwork.anuncio anun " +
              "INNER JOIN grindwork.localidade local on local.id = anun.localidade_id " +
              "INNER JOIN grindwork.usuario user on anun.usuario_id = user.id " +
              "WHERE anun.status = 1 ");

      if (servico != null && !servico.isEmpty()) {
        sql7.append("AND anun.titulo LIKE ? ");
      }

      if (localidade != null && !localidade.isEmpty()) {
        sql7.append("AND CONCAT(local.cidade, ', ', local.estado) = ? ");
      }

      sql7.append("LIMIT ? OFFSET ?");

      pstm7 = conn.prepareStatement(sql7.toString());

      int paramIndex = 1;

      if (servico != null && !servico.isEmpty()) {
        pstm7.setString(paramIndex++, "%" + servico + "%");
      }

      if (localidade != null && !localidade.isEmpty()) {
        pstm7.setString(paramIndex++, localidade);
      }

      pstm7.setInt(paramIndex++, pageRequest.getPageSize());
      pstm7.setInt(paramIndex++, pageRequest.getPageNumber() * pageRequest.getPageSize());

      rs7 = pstm7.executeQuery();

      while (rs7.next()) {
        Anuncio anuncio = new Anuncio();
        anuncio.setId(rs7.getLong("anun.id"));
        anuncio.setTitulo(rs7.getString("anun.titulo"));
        anuncio.setDescricao(rs7.getString("anun.descricao"));
        anuncio.setPreco(rs7.getDouble("anun.preco"));
        anuncio.setNota(rs7.getFloat("anun.nota"));
        anuncio.setEndereco(rs7.getString("anun.endereco"));
        anuncio.setDataCriacao(rs7.getString("anun.data_criacao"));
        anuncio.setImageUrl(rs7.getString("anun.imagem_url"));

        DadosUsuario user = new DadosUsuario();
        user.setId(rs7.getLong("anun.usuario_id"));
        user.setNome(Utilidades.capitalizeWords(rs7.getString("user.nome")));
        user.setEmail(rs7.getString("user.email"));
        user.setTelefone(rs7.getString("user.telefone"));

        anuncio.setUsuario(user);

        Localidade local = new Localidade();
        local.setId(rs7.getLong("local.id"));
        local.setCidade(rs7.getString("local.cidade"));
        local.setEstado(rs7.getString("local.estado"));

        anuncio.setLocalidade(local);

        ArrayList<Avaliacao> avaliacoes = new ArrayList<>();
        PreparedStatement pstmAval = conn.prepareStatement(
            "SELECT aval.id, aval.anuncio_id, aval.usuario_id, user.nome, aval.nota, aval.comentario, aval.data_avaliacao "
                +
                "FROM grindwork.avaliacao aval " +
                "INNER JOIN grindwork.usuario user on user.id = aval.usuario_id " +
                "WHERE aval.anuncio_id = ? " +
                "ORDER BY aval.data_avaliacao DESC LIMIT 6");
        pstmAval.setLong(1, anuncio.getId());

        ResultSet rsAval = pstmAval.executeQuery();
        while (rsAval.next()) {
          Avaliacao avaliacao = new Avaliacao();
          avaliacao.setId(rsAval.getLong("aval.id"));
          avaliacao.setAnuncioId(rsAval.getLong("aval.anuncio_id"));
          avaliacao.setUsuarioId(rsAval.getLong("usuario_id"));
          avaliacao.setUsuarioNome(Utilidades.capitalizeWords(rsAval.getString("user.nome")));
          avaliacao.setNota(rsAval.getFloat("aval.nota"));
          avaliacao.setComentario(rsAval.getString("aval.comentario"));
          avaliacao.setDataAvaliacao(rsAval.getString("aval.data_avaliacao"));
          avaliacoes.add(avaliacao);
        }
        rsAval.close();
        pstmAval.close();

        anuncio.setAvaliacao(avaliacoes);
        anuncios.add(anuncio);
      }

      // Contagem total para paginação
      StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM grindwork.anuncio WHERE status = 1");
      if (servico != null && !servico.isEmpty()) {
        countSql.append(" AND titulo LIKE ?");
      }
      if (localidade != null && !localidade.isEmpty()) {
        countSql.append(" AND endereco LIKE ?");
      }
      PreparedStatement countPstm = conn.prepareStatement(countSql.toString());

      paramIndex = 1;
      if (servico != null && !servico.isEmpty()) {
        countPstm.setString(paramIndex++, "%" + servico + "%");
      }
      if (localidade != null && !localidade.isEmpty()) {
        countPstm.setString(paramIndex++, "%" + localidade + "%");
      }

      ResultSet countRs = countPstm.executeQuery();
      countRs.next();
      long total = countRs.getLong(1);

      return new PageImpl<>(anuncios, pageRequest, total);

    } catch (Exception e) {
      System.out.println("Erro ao buscar anúncios: " + e.getMessage());
      throw new ObjectInternalErrorServiceException("Erro ao buscar anúncios.\n "+e.getMessage());
    } finally {
      try {
        if (rs7 != null)
          rs7.close();
        if (pstm7 != null)
          pstm7.close();
        if (conn != null)
          conn.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  public boolean findAnuncioById(Avaliacao avaliacao) {
    boolean temAvaliacao = false;
    conn = conexao.conectaMysql();
    PreparedStatement pstm13 = null;
    ResultSet rs13 = null;
    String sql13 = "";

    try {
      sql13 = " SELECT id FROM grindwork.avaliacao WHERE anuncio_id= ? and usuario_id = ? ";
      pstm13 = conn.prepareStatement(sql13);
      pstm13.setLong(1, avaliacao.getAnuncioId());
      pstm13.setLong(2, avaliacao.getUsuarioId());
      rs13 = pstm13.executeQuery();

      while (rs13.next()) {
        temAvaliacao = true;
      }
    } catch (Exception e) {
      System.out.println("Erro ao buscar avaliação: " + e.getMessage());
    } finally {
      try {
        if (conn != null) {
          conn.close();
          pstm13.close();
          rs13.close();
        }
        conn = null;
        pstm13 = null;
        rs13 = null;
      } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("Erro ao encerrar conexão: " + ex.getMessage());
      }
    }
    return temAvaliacao;
  }

  public Page<Anuncio> findMyAnuncioById(PageRequest pageRequest, String servico, Anuncio anun) {
    List<Anuncio> anuncios = new ArrayList<>();
    Connection conn = null;
    PreparedStatement pstm7 = null;
    ResultSet rs7 = null;
    try {
      conn = conexao.conectaMysql();

      StringBuilder sql7 = new StringBuilder(
          "SELECT anun.id, anun.titulo, anun.descricao, anun.preco, anun.nota, anun.usuario_id, user.nome, user.email, user.telefone, local.id, anun.endereco, anun.data_criacao, anun.imagem_url, local.cidade, local.estado "
              +
              "FROM grindwork.anuncio anun " +
              "INNER JOIN grindwork.localidade local on local.id = anun.localidade_id " +
              "INNER JOIN grindwork.usuario user on anun.usuario_id = user.id " +
              "WHERE anun.status = 1 AND user.id = ?");

      pstm7 = conn.prepareStatement(sql7.toString());

      int paramIndex = 1;

      pstm7.setLong(1, anun.getUsuario().getId());

      rs7 = pstm7.executeQuery();

      while (rs7.next()) {
        Anuncio anuncio = new Anuncio();
        anuncio.setId(rs7.getLong("anun.id"));
        anuncio.setTitulo(rs7.getString("anun.titulo"));
        anuncio.setDescricao(rs7.getString("anun.descricao"));
        anuncio.setPreco(rs7.getDouble("anun.preco"));
        anuncio.setNota(rs7.getFloat("anun.nota"));
        anuncio.setEndereco(rs7.getString("anun.endereco"));
        anuncio.setDataCriacao(rs7.getString("anun.data_criacao"));
        anuncio.setImageUrl(rs7.getString("anun.imagem_url"));

        DadosUsuario user = new DadosUsuario();
        user.setId(rs7.getLong("anun.usuario_id"));
        user.setNome(Utilidades.capitalizeWords(rs7.getString("user.nome")));
        user.setEmail(rs7.getString("user.email"));
        user.setTelefone(rs7.getString("user.telefone"));

        anuncio.setUsuario(user);

        Localidade local = new Localidade();
        local.setId(rs7.getLong("local.id"));
        local.setCidade(rs7.getString("local.cidade"));
        local.setEstado(rs7.getString("local.estado"));

        anuncio.setLocalidade(local);

        ArrayList<Avaliacao> avaliacoes = new ArrayList<>();
        PreparedStatement pstmAval = conn.prepareStatement(
            "SELECT aval.id, aval.anuncio_id as anuncio_id, aval.usuario_id, user.nome, aval.nota, aval.comentario, aval.data_avaliacao "
                +
                "FROM grindwork.avaliacao aval " +
                "INNER JOIN grindwork.usuario user on user.id = aval.usuario_id " +
                "WHERE aval.anuncio_id = ? " +
                "ORDER BY aval.data_avaliacao DESC LIMIT 6");
        pstmAval.setLong(1, anuncio.getId());

        ResultSet rsAval = pstmAval.executeQuery();
        while (rsAval.next()) {
          Avaliacao avaliacao = new Avaliacao();
          avaliacao.setId(rsAval.getLong("aval.id"));
          avaliacao.setAnuncioId(rsAval.getLong("aval.anuncio_id"));
          avaliacao.setUsuarioId(rsAval.getLong("usuario_id"));
          avaliacao.setUsuarioNome(Utilidades.capitalizeWords(rsAval.getString("user.nome")));
          avaliacao.setNota(rsAval.getFloat("aval.nota"));
          avaliacao.setComentario(rsAval.getString("aval.comentario"));
          avaliacao.setDataAvaliacao(rsAval.getString("aval.data_avaliacao"));
          avaliacoes.add(avaliacao);
        }
        rsAval.close();
        pstmAval.close();

        anuncio.setAvaliacao(avaliacoes);
        anuncios.add(anuncio);
      }

      // Contagem total para paginação
      StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM grindwork.anuncio WHERE status = 1");
      if (servico != null && !servico.isEmpty()) {
        countSql.append(" AND titulo LIKE ?");
      }
      PreparedStatement countPstm = conn.prepareStatement(countSql.toString());

      paramIndex = 1;
      if (servico != null && !servico.isEmpty()) {
        countPstm.setString(paramIndex++, "%" + servico + "%");
      }

      ResultSet countRs = countPstm.executeQuery();
      countRs.next();
      long total = countRs.getLong(1);

      return new PageImpl<>(anuncios, pageRequest, total);

    } catch (Exception e) {
      System.out.println("Erro ao buscar anúncios: " + e.getMessage());
      return Page.empty();
    } finally {
      try {
        if (rs7 != null)
          rs7.close();
        if (pstm7 != null)
          pstm7.close();
        if (conn != null)
          conn.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  public void atualizaNotaTotalAnuncio(double notaTotal, Long anuncio_id) {
    PreparedStatement pstm5 = null;
    String sql5 = "";

    try {
      conn = conexao.conectaMysql();
      StringBuilder sqlBuilder = new StringBuilder(
          "UPDATE grindwork.anuncio SET nota=? WHERE id=?");
      sql5 = sqlBuilder.toString();

      pstm5 = conn.prepareStatement(sql5);
      pstm5.setDouble(1, notaTotal);
      pstm5.setLong(2, anuncio_id);
      pstm5.execute();

    } catch (Exception e) {
      System.out.println("Erro ao atualizar nota do anúncio: " + e.getMessage());
      throw new ObjectInternalErrorServiceException("Erro ao atualizar nota do anúncio!\n"+ e.getMessage());
    } finally {
      try {
        if (conn != null) {
          conn.close();
          pstm5.close();
        }
        conn = null;
        pstm5 = null;
      } catch (Exception ex) {
        System.out.println("Erro ao encerrar conexão: " + ex.getMessage());
        throw new ObjectInternalErrorServiceException("Erro ao encerrar conexão!\n"+ ex.getMessage());
      }
    }
  }

}
