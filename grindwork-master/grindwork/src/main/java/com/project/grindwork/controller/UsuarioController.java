package com.project.grindwork.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.grindwork.dto.DownloadDTO;
import com.project.grindwork.dto.EmailDTO;
import com.project.grindwork.model.DadosUsuario;
import com.project.grindwork.model.Usuario;
import com.project.grindwork.service.AutenticacaoService;
import com.project.grindwork.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AutenticacaoService authService;

    private UsuarioController(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

    @PostMapping("/cadastrarUsuario")
    public ResponseEntity<String> cadastroUsuario(@RequestBody DadosUsuario usuario) {
        String status = usuarioService.cadastrarUsuario(usuario);
        authService.sendPasswordNewAccount(usuario);
        return ResponseEntity.ok().body(status);
    }

    @PostMapping("/excluirUsuario")
    public ResponseEntity<String> excluirUsuario(@RequestBody Usuario usuario) {
        String status = usuarioService.excluirUsuario(usuario.getEmail().trim());
        return ResponseEntity.ok().body(status);
    }

    @PostMapping("/dadosUsuario")
    public ResponseEntity<ArrayList<DadosUsuario>> dadosUsuario(@RequestBody Usuario usuario) {
        ArrayList<DadosUsuario> dados = usuarioService.dadosUsuario(usuario.getEmail().trim().toLowerCase());
        return ResponseEntity.ok().body(dados);
    }

    @PostMapping("/atualizarDadosUsuario")
    public ResponseEntity<ArrayList<DadosUsuario>> atualizarDadosUsuario(@RequestBody DadosUsuario usuario) {
        ArrayList<DadosUsuario> dados = usuarioService.atualizaDadosUsuario(usuario);
        return ResponseEntity.ok().body(dados);
    }

    @PostMapping("/esqueceuSenha")
    public ResponseEntity<Void> esqueceuSenha(@Valid @RequestBody EmailDTO objDTO) {
        authService.sendNewPassword(objDTO.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/fotoPerfil")
    public ResponseEntity<String> uploadFotosPerfil(@RequestParam("file") MultipartFile file) {
        String status = usuarioService.fotoPerfil(file);
        return ResponseEntity.ok().body(status);
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFotos(@PathVariable String fileName, HttpServletRequest request) {
        DownloadDTO download = usuarioService.downloadFoto(fileName, request);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + download.getResource().getFilename() + "\"")
                .body(download.getResource());
    }

    @GetMapping("/listarFotos")
    public ResponseEntity<List<String>> listarFotos() {
        List<String> fileNames = usuarioService.listarFotos();
        return ResponseEntity.ok(fileNames);
    }

}
