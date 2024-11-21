package com.project.grindwork.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.project.grindwork.dto.EnvioOrcamentoDTO;
import com.project.grindwork.exceptions.ObjectInternalErrorServiceException;
import com.project.grindwork.model.Usuario;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public abstract class AbstractEmailService implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendNewPasswordEmail(Usuario usuario, String newPass) {
        try {
            MimeMessage message = prepareNewPasswordEmail(usuario, newPass);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new ObjectInternalErrorServiceException("Erro ao enviar email: " + e.getMessage());
        }

    }

    @Override
    public void sendNewBudgetEmail(EnvioOrcamentoDTO cliente) {
        try {
            MimeMessage message = prepareNewBudgetEmail(cliente);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new ObjectInternalErrorServiceException("Erro ao enviar email: " + e.getMessage());
        }

    }

    @Override
    public void sendPasswordNewAccount(Usuario usuario, String newPass) {
        try {
            MimeMessage message = preparePasswordNewAccount(usuario, newPass);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new ObjectInternalErrorServiceException("Erro ao enviar email: " + e.getMessage());
        }

    }

    protected MimeMessage prepareNewPasswordEmail(Usuario usuario, String newPass) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(usuario.getEmail());
        helper.setSubject("[GrindWork] Redefinição de senha!");
        helper.setText(emailBodyNewPassword(usuario, newPass), true);
        return message;

    }

    protected MimeMessage prepareNewBudgetEmail(EnvioOrcamentoDTO cliente) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(cliente.getDestinatarioEmail());
        helper.setSubject("[GrindWork] Requisição de Orçamento!");
        helper.setText(emailBodyNewBudget(cliente), true);
        return message;

    }

    protected MimeMessage preparePasswordNewAccount(Usuario usuario, String newPass) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(usuario.getEmail());
        helper.setSubject("[GrindWork] Conta criada com sucesso!");
        helper.setText(emailBodyPasswordNewAccount(usuario, newPass), true);
        return message;

    }

    public String emailBodyNewPassword(Usuario usuario, String newPass) {
        return "<!DOCTYPE html>" +
                "<html lang=\"pt-BR\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>Nova Senha - GrindWork</title>" +
                "    <style>" +
                "        body {" +
                "            font-family: Arial, sans-serif;" +
                "            background-color: #f4f4f4;" +
                "            margin: 0;" +
                "            padding: 20px;" +
                "        }" +
                "        .container {" +
                "            max-width: 600px;" +
                "            margin: auto;" +
                "            background: white;" +
                "            padding: 20px;" +
                "            border-radius: 5px;" +
                "            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);" +
                "        }" +
                "        h1 {" +
                "            color: #333;" +
                "        }" +
                "        p {" +
                "            color: #555;" +
                "            font-size: 13px; " +
                "        }" +
                "        .footer {" +
                "            margin-top: 20px;" +
                "            font-size: 10px;" +
                "            color: #aaa;" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <h1>Olá " + usuario.getNome() + ",</h1>" +
                "        <p>Você recebeu este email porque uma nova senha foi criada para sua conta na GrindWork.</p>" +
                "        <p>Sua nova senha é: <strong>" + newPass + "</strong></p>" +
                "        <p>Por favor, guarde sua nova senha em um local seguro e não a compartilhe com ninguém.</p>" +
                "        <p>Se você não solicitou a alteração de senha, entre em contato com nosso suporte.</p>" +
                "        <p>Atenciosamente,<br><strong>A equipe da GrindWork</strong></p>" +
                "        <div class=\"footer\">" +
                "            <p>GrindWork - Transformando trabalho em resultados.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    public String emailBodyNewBudget(EnvioOrcamentoDTO cli) {
        return "<!DOCTYPE html>" +
                "<html lang=\"pt-BR\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>Solicitação de Orçamento - GrindWork</title>" +
                "    <style>" +
                "        body {" +
                "            font-family: Arial, sans-serif;" +
                "            background-color: #f4f4f4;" +
                "            margin: 0;" +
                "            padding: 20px;" +
                "        }" +
                "        .container {" +
                "            max-width: 600px;" +
                "            margin: auto;" +
                "            background: white;" +
                "            padding: 20px;" +
                "            border-radius: 5px;" +
                "            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);" +
                "        }" +
                "        h1 {" +
                "            color: #333;" +
                "        }" +
                "        p {" +
                "            color: #555;" +
                "            font-size: 13px; " +
                "        }" +
                "        .footer {" +
                "            margin-top: 20px;" +
                "            font-size: 10px;" +
                "            color: #aaa;" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <h1>Solicitação de Orçamento</h1>" +
                "        <p><strong>Nome do Cliente:</strong> " + cli.getClienteNome() + "</p>" +
                "        <p><strong>E-mail:</strong> " + cli.getClienteEmail() + "</p>" +
                "        <p><strong>Telefone:</strong> " + cli.getClienteTelefone() + "</p>" +
                "        <p><strong>Mensagem:</strong><br>" + cli.getClienteMensagem() + "</p><br /><br />" +
                "        <p>Este e-mail foi enviado para solicitar um orçamento para o serviço: <strong>"
                + cli.getNomeServico()
                + "</strong>.</p>" +
                "        <p>Por favor, entre em contato com o cliente para discutir os detalhes.</p>" +
                "        <p>Atenciosamente,<br><strong>A equipe da GrindWork</strong></p>" +
                "        <div class=\"footer\">" +
                "            <p>GrindWork - Transformando trabalho em resultados.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    public String emailBodyPasswordNewAccount(Usuario usuario, String newPass) {
        return "<!DOCTYPE html>" +
                "<html lang=\"pt-BR\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>Bem-vindo à GrindWork</title>" +
                "    <style>" +
                "        body {" +
                "            font-family: Arial, sans-serif;" +
                "            background-color: #f4f4f4;" +
                "            margin: 0;" +
                "            padding: 20px;" +
                "        }" +
                "        .container {" +
                "            max-width: 600px;" +
                "            margin: auto;" +
                "            background: white;" +
                "            padding: 20px;" +
                "            border-radius: 5px;" +
                "            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);" +
                "        }" +
                "        h1 {" +
                "            color: #333;" +
                "        }" +
                "        p {" +
                "            color: #555;" +
                "            font-size: 13px;" +
                "        }" +
                "        .footer {" +
                "            margin-top: 20px;" +
                "            font-size: 10px;" +
                "            color: #aaa;" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <h1>Bem-vindo(a) à GrindWork, " + usuario.getNome() + "!</h1>" +
                "        <p>É com grande satisfação que damos as boas-vindas à nossa plataforma.</p>" +
                "        <p>Para acessar sua conta, utilize a senha provisória a seguir. Recomendamos que altere sua senha no primeiro acesso para garantir maior segurança:</p>"
                +
                "        <p>Sua senha inicial é: <strong>" + newPass + "</strong></p>" +
                "        <p>Em caso de dúvidas ou necessidade de suporte, nossa equipe está à disposição.</p>" +
                "        <p>Atenciosamente,<br><strong>Equipe GrindWork</strong></p>" +
                "        <div class=\"footer\">" +
                "            <p>GrindWork - Transformando trabalho em resultados.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

}
