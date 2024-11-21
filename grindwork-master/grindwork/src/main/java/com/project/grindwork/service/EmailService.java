package com.project.grindwork.service;

import org.springframework.mail.SimpleMailMessage;

import com.project.grindwork.dto.EnvioOrcamentoDTO;
import com.project.grindwork.model.Usuario;

public interface EmailService {
    
    void sendEmail(SimpleMailMessage msg);

    void sendNewPasswordEmail(Usuario usuario, String newPass);
    
    void sendNewBudgetEmail(EnvioOrcamentoDTO cliente);

    void sendPasswordNewAccount(Usuario usuario, String newPass);
}
