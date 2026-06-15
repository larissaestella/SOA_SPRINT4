package com.careplus.map.controller;

import com.careplus.map.model.dto.CompletarMissaoDTO;
import com.careplus.map.model.dto.auth.LoginRequestDTO;
import com.careplus.map.model.dto.auth.RegisterRequestDTO;
import com.careplus.map.repository.MissaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
@DisplayName("AvatarController - Testes de Integração")
class AvatarControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MissaoRepository missaoRepository;

    private String tokenUsuario;
    private Long usuarioId;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequestDTO reg = new RegisterRequestDTO();
        reg.setNome("Diogo Teste");
        reg.setEmail("diogo_avatar@email.com");
        reg.setSenha("Senha@123");
        reg.setDataNascimento(LocalDate.of(1995, 3, 12));
        reg.setNomeAvatar("DiogoAvatar");

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated())
                .andReturn();

        usuarioId = objectMapper.readTree(regResult.getResponse().getContentAsString())
                .get("id").asLong();

        tokenUsuario = extrairToken("diogo_avatar@email.com", "Senha@123");
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
    @DisplayName("GET /usuarios/{id}/avatar - deve retornar avatar do usuário autenticado")
    void deveRetornarAvatarDoUsuario() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios/{id}/avatar", usuarioId)
                        .header("Authorization", "Bearer " + tokenUsuario))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeAvatar").value("DiogoAvatar"))
                .andExpect(jsonPath("$.nivel").value(1))
                .andExpect(jsonPath("$.pontosTotal").value(0))
                .andExpect(jsonPath("$.usuarioId").value(usuarioId));
    }

    @Test
    @DisplayName("GET /usuarios/{id}/avatar - deve retornar 401 sem token")
    void deveRetornar401AvatarSemToken() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios/{id}/avatar", usuarioId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /usuarios/{id}/missoes/completar - deve completar missão e atualizar avatar")
    void deveCompletarMissaoEAtualizarAvatar() throws Exception {
        Long missaoId = missaoRepository.findByAtivaTrue().get(0).getId();

        CompletarMissaoDTO dto = new CompletarMissaoDTO(missaoId, "Completei com sucesso!");

        mockMvc.perform(post("/api/v1/usuarios/{id}/missoes/completar", usuarioId)
                        .header("Authorization", "Bearer " + tokenUsuario)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(usuarioId))
                .andExpect(jsonPath("$.missaoId").value(missaoId))
                .andExpect(jsonPath("$.avatarAtualizado").exists())
                .andExpect(jsonPath("$.avatarAtualizado.pontosTotal").isNumber());
    }

    @Test
    @DisplayName("POST /usuarios/{id}/missoes/completar - deve retornar 404 para missão inexistente")
    void deveRetornar404MissaoInexistente() throws Exception {
        CompletarMissaoDTO dto = new CompletarMissaoDTO(99999L, null);

        mockMvc.perform(post("/api/v1/usuarios/{id}/missoes/completar", usuarioId)
                        .header("Authorization", "Bearer " + tokenUsuario)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /usuarios/{id}/estatisticas - deve retornar estatísticas do usuário")
    void deveRetornarEstatisticasDoUsuario() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios/{id}/estatisticas", usuarioId)
                        .header("Authorization", "Bearer " + tokenUsuario))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMissoesCompletadas").value(0))
                .andExpect(jsonPath("$.nivelAtual").value(1))
                .andExpect(jsonPath("$.pontosTotal").value(0))
                .andExpect(jsonPath("$.missoesPorCategoria").exists());
    }

    @Test
    @DisplayName("GET /usuarios/{id}/estatisticas - deve retornar 401 sem token")
    void deveRetornar401EstatisticasSemToken() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios/{id}/estatisticas", usuarioId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /ranking - deve retornar lista do ranking global")
    void deveRetornarRankingGlobal() throws Exception {
        mockMvc.perform(get("/api/v1/ranking")
                        .header("Authorization", "Bearer " + tokenUsuario))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)));
    }

    @Test
    @DisplayName("GET /ranking - deve retornar 401 sem token")
    void deveRetornar401RankingSemToken() throws Exception {
        mockMvc.perform(get("/api/v1/ranking"))
                .andExpect(status().isUnauthorized());
    }
}
