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
@DisplayName("MissaoController - Testes de Integração")
class MissaoControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String tokenUsuario;
    private String tokenAdmin;

    @BeforeEach
    void setUp() throws Exception {
        tokenUsuario = obterTokenUsuario("usuario_missao@email.com", PerfilUsuario.USUARIO);
        tokenAdmin = obterTokenAdmin("admin_missao@email.com");
    }

    private String obterTokenUsuario(String email, PerfilUsuario perfil) throws Exception {
        // Cria via registrar para perfil USUARIO
        RegisterRequestDTO reg = new RegisterRequestDTO();
        reg.setNome("Usuário");
        reg.setEmail(email);
        reg.setSenha("Senha@123");
        reg.setDataNascimento(LocalDate.of(1990, 1, 1));
        reg.setNomeAvatar("Av");

        mockMvc.perform(post("/api/v1/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        return extrairToken(email, "Senha@123");
    }

    private String obterTokenAdmin(String email) throws Exception {
        // Cria admin diretamente no repositório
        Usuario admin = Usuario.builder()
                .nome("Admin").email(email)
                .senha(passwordEncoder.encode("Senha@123"))
                .dataNascimento(LocalDate.of(1985, 1, 1))
                .perfil(PerfilUsuario.ADMIN).build();
        usuarioRepository.save(admin);

        return extrairToken(email, "Senha@123");
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

        String body = result.getResponse().getContentAsString();
        return objectMapper.readTree(body).get("token").asText();
    }

    @Test
    @DisplayName("GET /api/v1/missoes - deve listar missões com token válido")
    void deveListarMissoesComToken() throws Exception {
        mockMvc.perform(get("/api/v1/missoes")
                        .header("Authorization", "Bearer " + tokenUsuario))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)));
    }

    @Test
    @DisplayName("GET /api/v1/missoes - deve retornar 401 sem token")
    void deveRetornar401SemToken() throws Exception {
        mockMvc.perform(get("/api/v1/missoes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/missoes - ADMIN deve criar missão com sucesso")
    void adminDeveCriarMissao() throws Exception {
        String body = """
                {
                  "titulo": "Caminhada 30 min",
                  "descricao": "Caminhe por 30 minutos",
                  "categoria": "EXERCICIO",
                  "pontosRecompensa": 25,
                  "bonusExercicio": 20
                }
                """;

        mockMvc.perform(post("/api/v1/missoes")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Caminhada 30 min"))
                .andExpect(jsonPath("$.ativa").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/missoes - USUARIO não deve poder criar missão")
    void usuarioNaoDeveCriarMissao() throws Exception {
        String body = """
                {
                  "titulo": "Tentativa",
                  "descricao": "Não deve funcionar",
                  "categoria": "SAUDE",
                  "pontosRecompensa": 10
                }
                """;

        mockMvc.perform(post("/api/v1/missoes")
                        .header("Authorization", "Bearer " + tokenUsuario)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/v1/missoes com filtro de categoria")
    void deveListarMissoesComFiltroCategoria() throws Exception {
        mockMvc.perform(get("/api/v1/missoes?categoria=HIDRATACAO")
                        .header("Authorization", "Bearer " + tokenUsuario))
                .andExpect(status().isOk());
    }
}
