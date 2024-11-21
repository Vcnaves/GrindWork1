package com.project.grindwork.config;

import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Conexao {

    @Value("${spring.datasource.url}")
    private String URL;

    @Value("${spring.datasource.username}")
    private String USER;

    @Value("${spring.datasource.password}")
    private String PWD;
    
    public Connection conectaMysql() {

        try {
            return DriverManager.getConnection(URL,USER,PWD);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public String testeConexao(){
        Connection conn = null;
        String status = "";
        try {
            conn = conectaMysql();
            System.out.println("Conexão realizada com sucesso!");
            status = "Conexão realizada com sucesso!";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            status = e.getMessage();
        } finally{
            try {
                conn.close();
            } catch (Exception e) {
                System.out.println("Close connection: "+e.getMessage());
            }  
            System.out.println("Conexão encerrada!");          
        }
        return status;
    }
}
