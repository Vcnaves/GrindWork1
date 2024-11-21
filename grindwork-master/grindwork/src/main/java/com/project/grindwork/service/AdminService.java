package com.project.grindwork.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.grindwork.exceptions.ObjectNotFoundException;
import com.project.grindwork.model.Usuario;
import com.project.grindwork.repository.AdminRepository;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public ArrayList<Usuario> listaUsuarios() {
        return adminRepository.listarUsuarios();
    }

    public void atualizarPermissoes(Usuario usuario) {
        if(usuario.getId() == 0){
            throw new ObjectNotFoundException("Erro ao atualizar permissões!\n Id do usuário não encontrado.");
        }
        adminRepository.atualizarPerfil(usuario);
    }
    
    public void atualizarStatus(Usuario usuario) {
        if(usuario.getId() == 0){
            throw new ObjectNotFoundException("Erro ao atualizar status!\n Id do usuário não encontrado.");
        }
        adminRepository.atualizarStatusUsuario(usuario);
    }
}
