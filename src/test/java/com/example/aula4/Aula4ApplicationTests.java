package com.example.aula4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class Aula4ApplicationTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void contextLoads() {
    }

    // --- TESTES DE SEGURANÇA E PERFIS ---

    @Test
    void testLoginPagePublica() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void testRotaProtegidaSemAutenticacaoRedirecionaParaLogin() throws Exception {
        mockMvc.perform(get("/pacientes/list").accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testHttpBasicComAdmin() throws Exception {
        mockMvc.perform(get("/pacientes/list").with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(view().name("paciente/list"));
    }

    @Test
    void testLoginComUsuarioEmMemoriaUser() throws Exception {
        mockMvc.perform(formLogin("/login").user("user").password("123"))
                .andExpect(authenticated().withUsername("user").withRoles("USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consultas/list"));
    }

    @Test
    void testLoginComUsuarioEmMemoriaAdmin() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("admin"))
                .andExpect(authenticated().withUsername("admin").withRoles("ADMIN", "USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consultas/list"));
    }

    @Test
    void testLoginComSenhaInvalida() throws Exception {
        mockMvc.perform(formLogin("/login").user("user").password("errada"))
                .andExpect(unauthenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testLogout() throws Exception {
        mockMvc.perform(logout("/logout"))
                .andExpect(unauthenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testUserNaoAcessaRotasAdmin() throws Exception {
        mockMvc.perform(get("/pacientes/list"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/medicos/form"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/consultas/remove/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testHeaderExibeUsuarioAutenticado() throws Exception {
        mockMvc.perform(get("/consultas/list"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Olá,")))
                .andExpect(content().string(containsString("user")));
    }

    // --- TESTES DAS ROTAS EXISTENTES ---

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    void testPacientesList() throws Exception {
        mockMvc.perform(get("/pacientes/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("paciente/list"));
    }

    @Test
    void testPacientesForm() throws Exception {
        mockMvc.perform(get("/pacientes/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("paciente/form"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    void testPacientesHistorico() throws Exception {
        mockMvc.perform(get("/pacientes/historico/3"))
                .andExpect(status().isOk())
                .andExpect(view().name("paciente/historico"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testMedicosList() throws Exception {
        mockMvc.perform(get("/medicos/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("medico/list"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    void testMedicosForm() throws Exception {
        mockMvc.perform(get("/medicos/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("medico/form"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testMedicosHistorico() throws Exception {
        mockMvc.perform(get("/medicos/historico/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("medico/historico"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testConsultasList() throws Exception {
        mockMvc.perform(get("/consultas/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("consulta/list"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testConsultasForm() throws Exception {
        mockMvc.perform(get("/consultas/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("consulta/form"));
    }

    // --- TESTES DE VALIDAÇÃO ---

    @Test
    void testPacienteSaveInvalido() throws Exception {
        mockMvc.perform(post("/pacientes/save")
                        .with(csrf())
                        .param("nome", "")
                        .param("cpf", "123")
                        .param("email", "email-invalido")
                        .param("telefone", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("paciente/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("paciente", "nome", "cpf", "email", "telefone"));
    }

    @Test
    void testPacienteSaveValido() throws Exception {
        mockMvc.perform(post("/pacientes/save")
                        .with(csrf())
                        .param("nome", "Carlos Silva")
                        .param("cpf", "99988877766")
                        .param("email", "carlos.silva@email.com")
                        .param("telefone", "63984112233")
                        .param("convenio", "Unimed"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pacientes/list"))
                .andExpect(flash().attributeExists("mensagem"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    void testMedicoSaveInvalido() throws Exception {
        mockMvc.perform(post("/medicos/save")
                        .with(csrf())
                        .param("nome", "A")
                        .param("crm", "")
                        .param("cpf", "abc")
                        .param("email", "nao-eh-email")
                        .param("telefone", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("medico/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("medico", "nome", "crm", "cpf", "email", "telefone"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    void testMedicoSaveValido() throws Exception {
        mockMvc.perform(post("/medicos/save")
                        .with(csrf())
                        .param("nome", "Dr. Roberto Santos")
                        .param("crm", "789101")
                        .param("cpf", "88877766655")
                        .param("email", "roberto@medicina.com")
                        .param("telefone", "63999887766"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/medicos/list"))
                .andExpect(flash().attributeExists("mensagem"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testConsultaSaveInvalido() throws Exception {
        mockMvc.perform(post("/consultas/save")
                        .with(csrf())
                        .param("value", "-50.00")
                        .param("observation", "Teste"))
                .andExpect(status().isOk())
                .andExpect(view().name("consulta/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("consulta", "date", "value", "medico", "paciente"));
    }

    @Test
    void testPacienteNomeMuitoLongo() throws Exception {
        String nomeLongo = "A".repeat(101);
        mockMvc.perform(post("/pacientes/save")
                        .with(csrf())
                        .param("nome", nomeLongo)
                        .param("cpf", "11122233344")
                        .param("email", "teste@email.com")
                        .param("telefone", "63999998888"))
                .andExpect(status().isOk())
                .andExpect(view().name("paciente/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("paciente", "nome"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    void testConsultaRemove() throws Exception {
        mockMvc.perform(get("/consultas/remove/2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consultas/list"))
                .andExpect(flash().attributeExists("mensagem"));
    }
}
