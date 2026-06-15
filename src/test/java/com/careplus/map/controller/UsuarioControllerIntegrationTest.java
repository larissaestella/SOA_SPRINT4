package com.careplus.map.controller;

import com.careplus.map.model.dto.auth.LoginRequestDTO;
import com.careplus.map.model.dto.auth.RegisterRequestDTO;
import com.careplus.map.model.entity.Usuario;
import com.careplus.map.model.enums.PerfilUsuario;
import com.careplus.map.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("UsuarioController - Testes de Integração")
class UsuarioControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String tokenUsuario;
    private String tokenAdmin;
    private Long usuarioId;
    private Long adminId;

    @BeforeEach
    void setUp() throws Exception {
        // Cria usuário comum via endpoint público
        RegisterRequestDTO reg = new RegisterRequestDTO();
        reg.setNome("Jonata Teste");
        reg.setEmail("jonata_usuario@email.com");
        reg.setSenha("Senha@123");
        reg.setDataNascimento(LocalDate.of(2000, 7, 25));
        reg.setNomeAvatar("JonataAvatar");

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated())
                .andReturn();

        usuarioId = objectMapper.readTree(regResult.getResponse().getContentAsString())
                .get("id").asLong();

        tokenUsuario = extrairToken("jonata_usuario@email.com", "Senha@123");

        // Cria admin diretamente no repositório
        Usuario admin = Usuario.builder()
                .nome("Admin Controller").email("admin_usuario_ctrl@email.com")
                .senha(passwordEncoder.encode("Admin@123"))
                .dataNascimento(LocalDate.of(1980, 5, 10))
                .perfil(PerfilUsuario.ADMIN)
                .build();
        admin = usuarioRepository.save(admin);
        adminId = admin.getId();
        tokenAdmin = extrairToken("admin_usuario_ctrl@email.com", "Admin@123");
    }

    private String extrairToken(String email, String senha) throws Exception {
        LoginRequestDTO login = new LoginRequestDTO();
        login.setEmail(email);
        login.setSenha(senha);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }

    @Test
    @DisplayName("GET /usuarios/{id} - deve retornar dados do usuário")
    void deveRetornarDadosDoUsuario() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios/{id}", usuarioId)
                        .header("Authorization", "Bearer " + tokenUsuario))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId))
                .andExpect(jsonPath("$.nome").value("Jonata Teste"))
                .andExpect(jsonPath("$.email").value("jonata_usuario@email.com"));
    }

    @Test
    @DisplayName("GET /usuarios/{id} - deve retornar 401 sem token")
    void deveRetornar401SemToken() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios/{id}", usuarioId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /usuarios/{id} - deve retornar 404 para ID inexistente")
    void deveRetornar404UsuarioInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios/{id}", 99999L)
                        .header("Authorization", "Bearer " + tokenUsuario))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /usuarios - ADMIN pode listar todos os usuários")
    void adminPodeListarTodosUsuarios() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("GET /usuarios - USUARIO comum recebe 403")
    void usuarioNaoPodeListarTodos() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios")
                        .header("Authorization", "Bearer " + tokenUsuario))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /usuarios - deve retornar 401 sem token")
    void deveRetornar401ListaSemToken() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /usuarios/{id} - deve atualizar nome do usuário")
    void deveAtualizarNomeDoUsuario() throws Exception {
        String body = """
                {
                  "nome": "Jonata Atualizado",
                  "dataNascimento": "2000-07-25"
                }
                """;

        mockMvc.perform(put("/api/v1/usuarios/{id}", usuarioId)
                        .header("Authorization", "Bearer " + tokenUsuario)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Jonata Atualizado"));
    }

    @Test
    @DisplayName("PUT /usuarios/{id} - deve retornar 401 sem token")
    void deveRetornar401AtualizarSemToken() throws Exception {
        String body = """
                { "nome": "Qualquer" }
                """;

        mockMvc.perform(put("/api/v1/usuarios/{id}", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /usuarios/{id} - ADMIN pode remover usuário")
    void adminPodeRemoverUsuario() throws Exception {
        mockMvc.perform(delete("/api/v1/usuarios/{id}", usuarioId)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /usuarios/{id} - USUARIO recebe 403 ao tentar remover conta alheia")
    void usuarioNaoPodeRemoverConta() throws Exception {
        mockMvc.perform(delete("/api/v1/usuarios/{id}", adminId)
                        .header("Authorization", "Bearer " + tokenUsuario))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /usuarios/{id} - deve retornar 401 sem token")
    void deveRetornar401DeletarSemToken() throws Exception {
        mockMvc.perform(delete("/api/v1/usuarios/{id}", usuarioId))
                .andExpect(status().isUnauthorized());
    }
}
