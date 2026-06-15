package com.careplus.map.service;

import com.careplus.map.exception.RegraNegocioException;
import com.careplus.map.model.dto.UsuarioResponseDTO;
import com.careplus.map.model.dto.auth.LoginRequestDTO;
import com.careplus.map.model.dto.auth.LoginResponseDTO;
import com.careplus.map.model.dto.auth.RegisterRequestDTO;
import com.careplus.map.model.entity.Usuario;
import com.careplus.map.model.enums.PerfilUsuario;
import com.careplus.map.repository.UsuarioRepository;
import com.careplus.map.security.JwtService;
import com.careplus.map.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl - Testes Unitários")
class AuthServiceImplTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequestDTO registerDTO;
    private LoginRequestDTO loginDTO;
    private Usuario usuarioSalvo;

    @BeforeEach
    void setUp() {
        registerDTO = new RegisterRequestDTO();
        registerDTO.setNome("Maria Silva");
        registerDTO.setEmail("maria@email.com");
        registerDTO.setSenha("Senha@123");
        registerDTO.setDataNascimento(LocalDate.of(1995, 3, 20));
        registerDTO.setNomeAvatar("AvatarMaria");

        loginDTO = new LoginRequestDTO();
        loginDTO.setEmail("maria@email.com");
        loginDTO.setSenha("Senha@123");

        usuarioSalvo = Usuario.builder()
                .id(1L).nome("Maria Silva").email("maria@email.com")
                .senha("$2a$10$hash").dataNascimento(LocalDate.of(1995, 3, 20))
                .perfil(PerfilUsuario.USUARIO).build();
        // Simula @PrePersist
        try {
            var m = Usuario.class.getDeclaredMethod("onCreate");
            m.setAccessible(true);
            m.invoke(usuarioSalvo);
        } catch (Exception ignored) {}
    }

    @Test
    @DisplayName("Deve registrar novo usuário com sucesso")
    void deveRegistrarUsuarioComSucesso() {
        when(usuarioRepository.existsByEmail("maria@email.com")).thenReturn(false);
        when(passwordEncoder.encode("Senha@123")).thenReturn("$2a$10$hash");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        UsuarioResponseDTO resultado = authService.registrar(registerDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("maria@email.com");
        assertThat(resultado.getNome()).isEqualTo("Maria Silva");
        verify(passwordEncoder).encode("Senha@123");
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar com e-mail duplicado")
    void deveLancarExcecaoEmailDuplicado() {
        when(usuarioRepository.existsByEmail("maria@email.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(registerDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("e-mail");
    }

    @Test
    @DisplayName("Deve autenticar e retornar token JWT")
    void deveAutenticarERetornarToken() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(usuarioRepository.findByEmail("maria@email.com")).thenReturn(Optional.of(usuarioSalvo));
        when(jwtService.gerarToken("maria@email.com")).thenReturn("mocked.jwt.token");

        LoginResponseDTO resultado = authService.autenticar(loginDTO);

        assertThat(resultado.getToken()).isEqualTo("mocked.jwt.token");
        assertThat(resultado.getTipo()).isEqualTo("Bearer");
        assertThat(resultado.getEmail()).isEqualTo("maria@email.com");
    }
}
