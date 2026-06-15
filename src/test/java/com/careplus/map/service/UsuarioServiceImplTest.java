package com.careplus.map.service;

import com.careplus.map.exception.RecursoNaoEncontradoException;
import com.careplus.map.exception.RegraNegocioException;
import com.careplus.map.model.dto.UsuarioAtualizacaoDTO;
import com.careplus.map.model.dto.UsuarioResponseDTO;
import com.careplus.map.model.entity.Usuario;
import com.careplus.map.model.enums.PerfilUsuario;
import com.careplus.map.repository.UsuarioRepository;
import com.careplus.map.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioServiceImpl - Testes Unitários")
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuarioBase;

    @BeforeEach
    void setUp() {
        usuarioBase = Usuario.builder()
                .id(1L)
                .nome("Ana Paula")
                .email("ana@email.com")
                .senha("$2a$10$hash")
                .dataNascimento(LocalDate.of(1992, 4, 10))
                .perfil(PerfilUsuario.USUARIO)
                .build();

        // Simula @PrePersist
        try {
            var m = Usuario.class.getDeclaredMethod("onCreate");
            m.setAccessible(true);
            m.invoke(usuarioBase);
        } catch (Exception ignored) {}
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveListarTodosOsUsuarios() {
        Usuario outro = Usuario.builder()
                .id(2L).nome("Carlos").email("carlos@email.com")
                .senha("hash").dataNascimento(LocalDate.of(1988, 7, 20))
                .perfil(PerfilUsuario.USUARIO).build();
        try {
            var m = Usuario.class.getDeclaredMethod("onCreate");
            m.setAccessible(true);
            m.invoke(outro);
        } catch (Exception ignored) {}

        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioBase, outro));

        List<UsuarioResponseDTO> resultado = usuarioService.listar();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting("email")
                .containsExactly("ana@email.com", "carlos@email.com");
        verify(usuarioRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

        UsuarioResponseDTO resultado = usuarioService.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Ana Paula");
        assertThat(resultado.getEmail()).isEqualTo("ana@email.com");
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao buscar ID inexistente")
    void deveLancarExcecaoAoBuscarIdInexistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve atualizar nome e data de nascimento com sucesso")
    void deveAtualizarNomeEDataNascimento() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioBase);

        UsuarioAtualizacaoDTO dto = new UsuarioAtualizacaoDTO();
        dto.setNome("Ana Paula Silva");
        dto.setDataNascimento(LocalDate.of(1992, 4, 10));

        UsuarioResponseDTO resultado = usuarioService.atualizar(1L, dto);

        assertThat(usuarioBase.getNome()).isEqualTo("Ana Paula Silva");
        verify(usuarioRepository).save(usuarioBase);
    }

    @Test
    @DisplayName("Deve atualizar e-mail quando novo e-mail não está em uso")
    void deveAtualizarEmailDisponivel() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
        when(usuarioRepository.existsByEmail("novo@email.com")).thenReturn(false);
        when(usuarioRepository.save(any())).thenReturn(usuarioBase);

        UsuarioAtualizacaoDTO dto = new UsuarioAtualizacaoDTO();
        dto.setEmail("novo@email.com");

        usuarioService.atualizar(1L, dto);

        assertThat(usuarioBase.getEmail()).isEqualTo("novo@email.com");
        verify(usuarioRepository).existsByEmail("novo@email.com");
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar trocar para e-mail já existente")
    void deveLancarExcecaoEmailJaEmUso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
        when(usuarioRepository.existsByEmail("outro@email.com")).thenReturn(true);

        UsuarioAtualizacaoDTO dto = new UsuarioAtualizacaoDTO();
        dto.setEmail("outro@email.com");

        assertThatThrownBy(() -> usuarioService.atualizar(1L, dto))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("outro@email.com");
    }

    @Test
    @DisplayName("Não deve verificar disponibilidade ao manter o mesmo e-mail")
    void naoDeveVerificarEmailIgualAoAtual() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
        when(usuarioRepository.save(any())).thenReturn(usuarioBase);

        UsuarioAtualizacaoDTO dto = new UsuarioAtualizacaoDTO();
        dto.setEmail("ana@email.com"); // mesmo e-mail atual

        usuarioService.atualizar(1L, dto);

        // não deve chamar existsByEmail para o mesmo endereço
        verify(usuarioRepository, never()).existsByEmail(anyString());
    }

    @Test
    @DisplayName("Deve remover usuário com sucesso")
    void deveRemoverUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

        usuarioService.remover(1L);

        verify(usuarioRepository).delete(usuarioBase);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover usuário inexistente")
    void deveLancarExcecaoAoRemoverInexistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.remover(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(usuarioRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve retornar entidade Usuario ao chamar buscarEntidade")
    void deveRetornarEntidadeUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

        Usuario entidade = usuarioService.buscarEntidade(1L);

        assertThat(entidade).isNotNull();
        assertThat(entidade.getId()).isEqualTo(1L);
        assertThat(entidade.getEmail()).isEqualTo("ana@email.com");
    }
}
