package com.project.grindwork.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.userdetails.UserDetails;

import com.project.grindwork.exceptions.AuthorizationException;
import com.project.grindwork.model.DadosAutenticacao;
import com.project.grindwork.model.Usuario;
import com.project.grindwork.security.DadosTokenJWT;
import com.project.grindwork.security.TokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @SuppressWarnings("rawtypes")
    @PostMapping
    public ResponseEntity efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        try{
            var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
            var authentication = manager.authenticate(authenticationToken);
            var tokenJWT = tokenService.gerarToken(Usuario.fromUserDetails((UserDetails) authentication.getPrincipal()));
            return ResponseEntity.ok().body(new DadosTokenJWT(tokenJWT));
        }catch(Exception e){
            throw new AuthorizationException("Email ou senha invalido!");
        }
    }
}
