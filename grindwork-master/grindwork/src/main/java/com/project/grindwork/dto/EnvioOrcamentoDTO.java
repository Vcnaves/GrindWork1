package com.project.grindwork.dto;

public class EnvioOrcamentoDTO {
    private String clienteNome;
    private String clienteEmail;
    private String clienteTelefone;
    private String clienteMensagem;
    private String nomeServico;
    private String destinatarioEmail;
    
    public EnvioOrcamentoDTO(String clienteNome, String clienteEmail, String clienteTelefone, String clienteMensagem, String nomeServico, String destinatarioEmail) {
        this.clienteNome = clienteNome;
        this.clienteEmail = clienteEmail;
        this.clienteTelefone = clienteTelefone;
        this.clienteMensagem = clienteMensagem;
        this.nomeServico = nomeServico;
        this.destinatarioEmail = destinatarioEmail;
    }
    public String getClienteNome() {
        return clienteNome;
    }
    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }
    public String getClienteEmail() {
        return clienteEmail;
    }
    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }
    public String getClienteTelefone() {
        return clienteTelefone;
    }
    public void setClienteTelefone(String clienteTelefone) {
        this.clienteTelefone = clienteTelefone;
    }
    public String getClienteMensagem() {
        return clienteMensagem;
    }
    public void setClienteMensagem(String clienteMensagem) {
        this.clienteMensagem = clienteMensagem;
    }
    public String getNomeServico() {
        return nomeServico;
    }
    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }
    public String getDestinatarioEmail() {
        return destinatarioEmail;
    }
    public void setDestinatarioEmail(String destinatarioEmail) {
        this.destinatarioEmail = destinatarioEmail;
    }

}
