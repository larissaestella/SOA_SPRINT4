package com.careplus.map.service;

import com.careplus.map.exception.RecursoNaoEncontradoException;
import com.careplus.map.exception.RegraNegocioException;
import com.careplus.map.model.dto.MissaoCadastroDTO;
import com.careplus.map.model.dto.MissaoResponseDTO;
import com.careplus.map.model.entity.Missao;
import com.careplus.map.model.enums.CategoriaMissao;
import com.careplus.map.repository.MissaoRepository;
import com.careplus.map.service.impl.MissaoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MissaoServiceImpl - Testes Unitários")
class MissaoServiceImplTest {

    @Mock
    private MissaoRepository missaoRepository;

    @InjectMocks
    private MissaoServiceImpl missaoService;

    private Missao missaoAtiva;
    private MissaoCadastroDTO cadastroDTO;

    @BeforeEach
    void setUp() {
        missaoAtiva = Missao.builder()
                .id(1L)
                .titulo("Beber 2L de água")
                .descricao("Hidrate-se ao longo do dia")
                .categoria(CategoriaMissao.HIDRATACAO)
                .pontosRecompensa(20)
                .bonusHidratacao(15)
                .bonusSaude(5)
                .bonusSono(0)
                .bonusExercicio(0)
                .bonusBemEstar(0)
                .ativa(true)
                .build();

        cadastroDTO = new MissaoCadastroDTO();
        cadastroDTO.setTitulo("Beber 2L de água");
        cadastroDTO.setDescricao("Hidrate-se ao longo do dia");
        cadastroDTO.setCategoria(CategoriaMissao.HIDRATACAO);
        cadastroDTO.setPontosRecompensa(20);
        cadastroDTO.setBonusHidratacao(15);
        cadastroDTO.setBonusSaude(5);
    }

    @Test
    @DisplayName("Deve criar missão com sucesso")
    void deveCriarMissaoComSucesso() {
        when(missaoRepository.save(any(Missao.class))).thenReturn(missaoAtiva);

        MissaoResponseDTO resultado = missaoService.criar(cadastroDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTitulo()).isEqualTo("Beber 2L de água");
        assertThat(resultado.getCategoria()).isEqualTo(CategoriaMissao.HIDRATACAO);
        assertThat(resultado.getPontosRecompensa()).isEqualTo(20);
        verify(missaoRepository).save(any(Missao.class));
    }

    @Test
    @DisplayName("Deve listar apenas missões ativas sem filtro de categoria")
    void deveListarMissoesAtivasSemFiltro() {
        when(missaoRepository.findByAtivaTrue()).thenReturn(List.of(missaoAtiva));

        List<MissaoResponseDTO> resultado = missaoService.listar(null);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getAtiva()).isTrue();
        verify(missaoRepository).findByAtivaTrue();
        verify(missaoRepository, never()).findByAtivaAndCategoria(anyBoolean(), any());
    }

    @Test
    @DisplayName("Deve listar missões filtradas por categoria")
    void deveListarMissoesFiltradaPorCategoria() {
        when(missaoRepository.findByAtivaAndCategoria(true, CategoriaMissao.HIDRATACAO))
                .thenReturn(List.of(missaoAtiva));

        List<MissaoResponseDTO> resultado = missaoService.listar(CategoriaMissao.HIDRATACAO);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCategoria()).isEqualTo(CategoriaMissao.HIDRATACAO);
        verify(missaoRepository).findByAtivaAndCategoria(true, CategoriaMissao.HIDRATACAO);
    }

    @Test
    @DisplayName("Deve buscar missão por ID com sucesso")
    void deveBuscarMissaoPorIdComSucesso() {
        when(missaoRepository.findById(1L)).thenReturn(Optional.of(missaoAtiva));

        MissaoResponseDTO resultado = missaoService.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getTitulo()).isEqualTo("Beber 2L de água");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar missão com ID inexistente")
    void deveLancarExcecaoAoBuscarMissaoInexistente() {
        when(missaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> missaoService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve desativar missão com soft-delete")
    void deveDesativarMissao() {
        when(missaoRepository.findById(1L)).thenReturn(Optional.of(missaoAtiva));
        when(missaoRepository.save(any(Missao.class))).thenReturn(missaoAtiva);

        missaoService.desativar(1L);

        assertThat(missaoAtiva.getAtiva()).isFalse();
        verify(missaoRepository).save(missaoAtiva);
    }

    @Test
    @DisplayName("Deve atualizar dados da missão")
    void deveAtualizarMissao() {
        when(missaoRepository.findById(1L)).thenReturn(Optional.of(missaoAtiva));
        cadastroDTO.setTitulo("Beber 3L de água");
        Missao atualizada = Missao.builder().id(1L).titulo("Beber 3L de água")
                .descricao("Hidrate-se").categoria(CategoriaMissao.HIDRATACAO)
                .pontosRecompensa(25).bonusSaude(0).bonusHidratacao(0)
                .bonusSono(0).bonusExercicio(0).bonusBemEstar(0).ativa(true).build();
        when(missaoRepository.save(any())).thenReturn(atualizada);

        MissaoResponseDTO resultado = missaoService.atualizar(1L, cadastroDTO);

        assertThat(resultado.getTitulo()).isEqualTo("Beber 3L de água");
    }
}
