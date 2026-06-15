package com.careplus.map.controller;

import com.careplus.map.model.dto.auth.LoginRequestDTO;
import com.careplus.map.model.dto.auth.RegisterRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthController - Testes de Integração")
class AuthControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private RegisterRequestDTO buildRegisterDTO(String email) {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setNome("Usuário Teste");
        dto.setEmail(email);
        dto.setSenha("Senha@123");
        dto.setDataNascimento(LocalDate.of(1995, 6, 15));
        dto.setNomeAvatar("AvatarTeste");
        return dto;
    }

    @Test
    @DisplayName("POST /api/v1/auth/registrar - deve criar usuário e retornar 201")
    void deveRegistrarUsuarioComSucesso() throws Exception {
        RegisterRequestDTO dto = buildRegisterDTO("novo@email.com");

        mockMvc.perform(post("/api/v1/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("novo@email.com"))
                .andExpect(jsonPath("$.nome").value("Usuário Teste"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/registrar - deve retornar 400 com e-mail duplicado")
    void deveRetornar400ComEmailDuplicado() throws Exception {
        RegisterRequestDTO dto = buildRegisterDTO("duplicado@email.com");

        // Primeiro registro
        mockMvc.perform(post("/api/v1/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // Segundo com mesmo e-mail — regra de negócio violada retorna 422
        mockMvc.perform(post("/api/v1/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - deve autenticar e retornar token JWT")
    void deveAutenticarERetornarToken() throws Exception {
        // Registra
        RegisterRequestDTO reg = buildRegisterDTO("login@email.com");
        mockMvc.perform(post("/api/v1/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        // Faz login
        LoginRequestDTO login = new LoginRequestDTO();
        login.setEmail("login@email.com");
        login.setSenha("Senha@123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.email").value("login@email.com"))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(body).contains("token");
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - deve retornar 401 com senha errada")
    void deveRetornar401ComSenhaErrada() throws Exception {
        RegisterRequestDTO reg = buildRegisterDTO("wrong@email.com");
        mockMvc.perform(post("/api/v1/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        LoginRequestDTO login = new LoginRequestDTO();
        login.setEmail("wrong@email.com");
        login.setSenha("SenhaErrada");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/auth/registrar - deve retornar 400 com dados inválidos")
    void deveRetornar400ComDadosInvalidos() throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO(); // sem campos obrigatórios

        mockMvc.perform(post("/api/v1/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
