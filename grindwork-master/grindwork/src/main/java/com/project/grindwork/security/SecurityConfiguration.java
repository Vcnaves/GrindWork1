package com.project.grindwork.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.project.grindwork.service.EmailService;
import com.project.grindwork.service.SmtpEmailService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final SecurityFilter securityFilter;

    public SecurityConfiguration(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    private static final String[] PUBLIC_MATCHERS_POST = {
            "/login",
            "/usuario/cadastrarUsuario",
            "/usuario/esqueceuSenha",
    };

    private static final String[] PUBLIC_MATCHERS_GET = {
        "/anuncio/listaAnuncioPaginado",
        "/localidades/estados-cidades",
        "/usuario/download/**",
        "/anuncio/download/**"
};

    private static final String[] ADMIN_MATCHERS_POST = {
            "/admin/**"
    };

    private static final String[] MATCHERS_POST = {
            "/anuncio/enviarOrcamento",
            "/anuncio/inserirAvaliacao",
            "/anuncio/excluirAnuncio",
            "/anuncio/cadastrarAnuncio",
            "/usuario/fotoPerfil",
            "/usuario/excluirUsuario",
            "/usuario/dadosUsuario",
            "/usuario/atualizarDadosUsuario",
            "/anuncio/meusAnuncioPaginado",
            "/anuncio/atualizarAnuncio"
    };

    private static final String[] MATCHERS_GET = {
            "/anuncio/listaAnuncio",
            "/anuncio/listarAvaliacao",
            "/usuario/listarFotos"
    };

    private static final String[] MATCHERS_DELETE = {
            "/anuncio/excluirAnuncio"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults()) // Habilita CORS com as configurações padrão
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    req.requestMatchers(HttpMethod.POST, PUBLIC_MATCHERS_POST).permitAll();
                    req.requestMatchers(HttpMethod.GET, PUBLIC_MATCHERS_GET).permitAll();
                    req.requestMatchers(HttpMethod.POST, ADMIN_MATCHERS_POST).hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.GET, MATCHERS_GET).hasAnyRole("USER", "ADMIN");
                    req.requestMatchers(HttpMethod.POST, MATCHERS_POST).hasAnyRole("USER", "ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, MATCHERS_DELETE).hasAnyRole("USER", "ADMIN");
                    req.anyRequest().authenticated();
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public EmailService EmailService() {
        return new SmtpEmailService();
    }

}
