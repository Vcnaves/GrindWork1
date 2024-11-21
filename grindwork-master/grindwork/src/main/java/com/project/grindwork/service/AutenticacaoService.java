package com.project.grindwork.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.grindwork.exceptions.ObjectNotFoundException;
import com.project.grindwork.model.DadosUsuario;
import com.project.grindwork.model.Usuario;
import com.project.grindwork.repository.UsuarioRepository;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsuarioService usuarioService;

    private Random rand = new Random();

    private AutenticacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
      }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByLogin(email);
    }

    public void sendNewPassword(String email){
        Usuario usuario = usuarioRepository.findByLogin(email);
        if (usuario == null) {
            throw new ObjectNotFoundException("Email não encontrado!");
        }
        String newPass = newPassword();
        usuario.setSenha(usuarioService.encriptarSenha(newPass));

        usuarioRepository.alterarSenha(usuario);
        emailService.sendNewPasswordEmail(usuario, newPass);
    }

    public void sendPasswordNewAccount(DadosUsuario usuario){
        if (usuario == null) {
            throw new ObjectNotFoundException("Email não encontrado!");
        }
        String newPass = newPassword();
        usuario.setSenha(usuarioService.encriptarSenha(newPass));

        usuarioRepository.alterarSenha(usuario);
        emailService.sendPasswordNewAccount(usuario, newPass);
    }

    private String newPassword() {
        char[] vet = new char[10];
        for (int i = 0; i < 10; i++) {
            vet[i] = randomChar();
        }
        return new String(vet);
    }

    private char randomChar() {
        int opt = rand.nextInt(3);
        if (opt == 0) {// gera um digito
            return (char) (rand.nextInt(10) + 48);
        } else if (opt == 1) {// gera letra maiuscula
            return (char) (rand.nextInt(26) + 65);
        } else {// gera letra minuscula
            return (char) (rand.nextInt(26) + 97);
        }
    }

}
