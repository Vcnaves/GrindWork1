package com.project.grindwork.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.grindwork.model.Usuario;
import com.project.grindwork.service.AdminService;


@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;

    @GetMapping("/usuarios")
    public ResponseEntity<ArrayList<Usuario>>  postMethodName() {
        ArrayList<Usuario> listaUser = adminService.listaUsuarios(); 
        return ResponseEntity.ok().body(listaUser);
    }

    @PostMapping("/atualizaPermissoes")
    public ResponseEntity<String> atualizaPermissoes(@RequestBody Usuario usuario) {
        adminService.atualizarPermissoes(usuario);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/atualizaStatus")
    public ResponseEntity<String> atualizaStatus(@RequestBody Usuario usuario) {
        adminService.atualizarStatus(usuario);
        return ResponseEntity.ok().build();
    }
    
    
}
