package com.project.grindwork.controller;

import com.project.grindwork.dto.AnuncioDTO;
import com.project.grindwork.dto.DownloadDTO;
import com.project.grindwork.dto.EnvioOrcamentoDTO;
import com.project.grindwork.model.Anuncio;
import com.project.grindwork.model.Avaliacao;
import com.project.grindwork.model.DadosUsuario;
import com.project.grindwork.model.Localidade;
import com.project.grindwork.service.AnuncioService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/anuncio")
public class AnuncioController {

  @Autowired
  private AnuncioService anuncioService;

  private AnuncioController(AnuncioService anuncioService) {
    this.anuncioService = anuncioService;
  }

  @PostMapping("/cadastrarAnuncio")
  public ResponseEntity<String> cadastroAnuncio(
      @RequestParam("usuario_id") int usuario_id,
      @RequestParam("titulo") String titulo,
      @RequestParam("descricao") String descricao,
      @RequestParam("endereco") String endereco,
      @RequestParam("valor") Double valor,
      @RequestParam(value = "imagem", required = false) MultipartFile imagem,
      @RequestParam("localidade") String localidade) {

    String[] partesLocalidade = localidade.split(",\\s*");
    Localidade local = new Localidade();

    if (partesLocalidade.length == 2) {
      local.setCidade(partesLocalidade[0]);
      local.setEstado(partesLocalidade[1]);
    } else {
      return ResponseEntity.badRequest().body("Formato de localidade inválido. Utilize 'cidade, estado'.");
    }

    DadosUsuario usuario = new DadosUsuario();
    usuario.setId(usuario_id);

    Anuncio anuncio = new Anuncio();
    anuncio.setUsuario(usuario);
    anuncio.setTitulo(titulo);
    anuncio.setDescricao(descricao);
    anuncio.setEndereco(endereco);
    anuncio.setPreco(valor);
    anuncio.setLocalidade(local);

    String status = anuncioService.cadastrarAnuncio(anuncio, imagem);
    return ResponseEntity.ok().body(status);
  }

  @GetMapping("/listarAnuncio")
  public ResponseEntity<ArrayList<Anuncio>> listaAnuncio(
      @RequestBody Anuncio anuncio) {
    ArrayList<Anuncio> lista = anuncioService.listarAnuncio(
        anuncio.getLocalidade().getId());
    return ResponseEntity.ok().body(lista);
  }

  @DeleteMapping("/excluirAnuncio")
  public ResponseEntity<String> excluirAnuncio(
      @RequestBody Anuncio anuncio) {
    String status = anuncioService.excluirAnuncio(anuncio.getId());
    return ResponseEntity.ok().body(status);
  }
  
  @PostMapping("/atualizarAnuncio")
  public ResponseEntity<String> atualizaAnuncio(
      @RequestParam("anuncio_id") int anuncio_id,
      @RequestParam("titulo") String titulo,
      @RequestParam("descricao") String descricao,
      @RequestParam("endereco") String endereco,
      @RequestParam("valor") Double valor,
      @RequestParam(value = "imagem", required = false)  MultipartFile imagem,
      @RequestParam("localidade") String localidade) {

    String[] partesLocalidade = localidade.split(",\\s*");
    Localidade local = new Localidade();

    if (partesLocalidade.length == 2) {
      local.setCidade(partesLocalidade[0]);
      local.setEstado(partesLocalidade[1]);
    } else {
      return ResponseEntity.badRequest().body("Formato de localidade inválido. Utilize 'cidade, estado'.");
    }

    Anuncio anuncio = new Anuncio();
    anuncio.setId(anuncio_id);
    anuncio.setTitulo(titulo);
    anuncio.setDescricao(descricao);
    anuncio.setEndereco(endereco);
    anuncio.setPreco(valor);
    anuncio.setLocalidade(local);

    String status = anuncioService.atualizarAnuncio(anuncio, imagem);
    return ResponseEntity.ok().body(status);
  }

  @PostMapping("/inserirAvaliacao")
  public ResponseEntity<String> inserirAvaliacao(
      @RequestBody Avaliacao avaliacao) {
    String status = anuncioService.inserirAvaliacao(avaliacao);
    return ResponseEntity.ok().body(status);
  }

  @GetMapping("/listarAvaliacao")
  public ResponseEntity<ArrayList<Avaliacao>> listarAvaliacao(
      @RequestBody Avaliacao avaliacao) {
    ArrayList<Avaliacao> lista = anuncioService.listarAvaliacao(avaliacao.getAnuncioId());
    return ResponseEntity.ok().body(lista);
  }

  @GetMapping("/listaAnuncioPaginado")
  public ResponseEntity<Page<AnuncioDTO>> listarAnuncioPage(
      @RequestParam(value = "page", defaultValue = "0") Integer page,
      @RequestParam(value = "linesPerPage", defaultValue = "24") Integer linesPerPage,
      @RequestParam(value = "orderBy", defaultValue = "none") String orderBy,
      @RequestParam(value = "direction", defaultValue = "ASC") String direction,
      @RequestParam(value = "servico", required = false) String servico,
      @RequestParam(value = "localidade", required = false) String localidade) {

    Page<Anuncio> lista = anuncioService.findPage(page, linesPerPage, orderBy, direction, servico, localidade);
    Page<AnuncioDTO> listDTO = lista.map(obj -> new AnuncioDTO(obj));
    return ResponseEntity.ok().body(listDTO);
  }

  @PostMapping("/enviarOrcamento")
  public ResponseEntity<String> postMethodName(@RequestBody EnvioOrcamentoDTO cliente) {
    anuncioService.sendNewBudget(cliente);
    return ResponseEntity.ok().body("OK");
  }

  @GetMapping("/download/{fileName:.+}")
  public ResponseEntity<Resource> downloadFotos(@PathVariable String fileName, HttpServletRequest request) {
    DownloadDTO download = anuncioService.downloadFoto(fileName, request);
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(download.getContentType()))
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "inline; filename=\"" + download.getResource().getFilename() + "\"")
        .body(download.getResource());
  }

  @PostMapping("/meusAnuncioPaginado")
  public ResponseEntity<Page<AnuncioDTO>> listarMeusAnuncio(
      @RequestParam(value = "page", defaultValue = "0") Integer page,
      @RequestParam(value = "linesPerPage", defaultValue = "24") Integer linesPerPage,
      @RequestParam(value = "orderBy", defaultValue = "none") String orderBy,
      @RequestParam(value = "direction", defaultValue = "ASC") String direction,
      @RequestParam(value = "servico", required = false) String servico,
      @RequestParam(value = "usuarioId", required = true) Long usuarioId) {

    Anuncio anuncio = new Anuncio();
    DadosUsuario user = new DadosUsuario();
    user.setId(usuarioId);
    anuncio.setUsuario(user);
    Page<Anuncio> lista = anuncioService.findMyAnuncioById(page, linesPerPage, orderBy, direction, servico, anuncio);
    Page<AnuncioDTO> listDTO = lista.map(obj -> new AnuncioDTO(obj));
    return ResponseEntity.ok().body(listDTO);
  }

}
