package com.example.aula4.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration // classe de configuração
@EnableWebSecurity // indica que serão definidas configurações personalizadas de segurança
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/pacientes/form").permitAll()
                        .requestMatchers(HttpMethod.POST, "/pacientes/save").permitAll()
                        .requestMatchers(
                                "/pacientes/list",
                                "/pacientes/edit/**",
                                "/pacientes/update",
                                "/pacientes/remove/**",
                                "/pacientes/historico/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                "/medicos/form",
                                "/medicos/save",
                                "/medicos/edit/**",
                                "/medicos/update",
                                "/medicos/remove/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                "/consultas/edit/**",
                                "/consultas/update",
                                "/consultas/remove/**"
                        ).hasRole("ADMIN")
                        .anyRequest().authenticated() // demais requisições exigem usuário autenticado
                )
                .formLogin(form -> form
                        .loginPage("/login") // URL da página de login que criaremos
                        .defaultSuccessUrl("/consultas/list", true)
                        .permitAll() // a página de login é acessível sem autenticação
                )
                .httpBasic(withDefaults()) // autenticação básica (usuário e senha no cabeçalho HTTP)
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .rememberMe(withDefaults()); // mantém o usuário autenticado após fechar o navegador

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("123"))
                .roles("USER")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin"))
                .roles("ADMIN", "USER")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    /**
     * Instancia o codificador BCrypt e deixa seu controle sob responsabilidade do Spring.
     * Sempre que o Spring Security precisar codificar ou comparar uma senha, utilizará este bean.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
