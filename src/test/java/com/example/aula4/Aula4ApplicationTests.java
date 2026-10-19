package com.example.aula4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void contextLoads() {
    }

    @Test
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
    void testPacientesHistorico() throws Exception {
        mockMvc.perform(get("/pacientes/historico/3"))
                .andExpect(status().isOk())
                .andExpect(view().name("paciente/historico"));
    }

    @Test
    void testMedicosList() throws Exception {
        mockMvc.perform(get("/medicos/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("medico/list"));
    }

    @Test
    void testMedicosForm() throws Exception {
        mockMvc.perform(get("/medicos/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("medico/form"));
    }

    @Test
    void testMedicosHistorico() throws Exception {
        mockMvc.perform(get("/medicos/historico/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("medico/historico"));
    }

    @Test
    void testConsultasList() throws Exception {
        mockMvc.perform(get("/consultas/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("consulta/list"));
    }

    @Test
    void testConsultasForm() throws Exception {
        mockMvc.perform(get("/consultas/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("consulta/form"));
    }

    // --- TESTES DE VALIDAÇÃO ---

    @Test
    void testPacienteSaveInvalido() throws Exception {
        // Envia campos em branco/inválidos -> deve retornar o formulário com erros de validação
        mockMvc.perform(post("/pacientes/save")
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
        // Envia dados válidos -> deve redirecionar para a lista com mensagem de sucesso
        mockMvc.perform(post("/pacientes/save")
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
    void testMedicoSaveInvalido() throws Exception {
        // Envia médico sem CRM e com e-mail inválido
        mockMvc.perform(post("/medicos/save")
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
    void testMedicoSaveValido() throws Exception {
        mockMvc.perform(post("/medicos/save")
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
    void testConsultaSaveInvalido() throws Exception {
        // Envia consulta sem data, valor negativo, e sem selecionar médico e paciente
        mockMvc.perform(post("/consultas/save")
                        .param("value", "-50.00")
                        .param("observation", "Teste"))
                .andExpect(status().isOk())
                .andExpect(view().name("consulta/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("consulta", "date", "value", "medico", "paciente"));
    }

    @Test
    void testPacienteNomeMuitoLongo() throws Exception {
        // Mais de 100 caracteres no nome (conforme tabela 1.8 do PDF) -> Erro de tamanho do nome
        String nomeLongo = "A".repeat(101);
        mockMvc.perform(post("/pacientes/save")
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
    void testConsultaRemove() throws Exception {
        mockMvc.perform(get("/consultas/remove/2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consultas/list"))
                .andExpect(flash().attributeExists("mensagem"));
    }
}
