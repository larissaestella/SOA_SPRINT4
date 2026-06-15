package com.careplus.map.service;

import com.careplus.map.exception.RegraNegocioException;
import com.careplus.map.model.dto.AvatarResponseDTO;
import com.careplus.map.model.dto.CompletarMissaoDTO;
import com.careplus.map.model.dto.MissaoCompletadaResponseDTO;
import com.careplus.map.model.entity.*;
import com.careplus.map.model.enums.CategoriaMissao;
import com.careplus.map.model.enums.PerfilUsuario;
import com.careplus.map.repository.AvatarRepository;
import com.careplus.map.repository.MissaoCompletadaRepository;
import com.careplus.map.service.impl.AvatarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AvatarServiceImpl - Testes Unitários")
class AvatarServiceImplTest {

    @Mock private AvatarRepository avatarRepository;
    @Mock private MissaoCompletadaRepository missaoCompletadaRepository;
    @Mock private IUsuarioService usuarioService;
    @Mock private IMissaoService missaoService;

    @InjectMocks
    private AvatarServiceImpl avatarService;

    private Usuario usuario;
    private Avatar avatar;
    private Missao missao;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L).nome("João").email("joao@email.com")
                .senha("hash").dataNascimento(LocalDate.of(1990, 1, 1))
                .perfil(PerfilUsuario.USUARIO).build();

        avatar = Avatar.builder()
                .id(1L).usuario(usuario)
                .nomeAvatar("AvatarJoao")
                .nivel(1).pontosTotal(0)
                .saude(0).hidratacao(0).sono(0).exercicio(0).bemEstar(0)
                .build();

        missao = Missao.builder()
                .id(1L).titulo("Beber água").descricao("Beba 2L")
                .categoria(CategoriaMissao.HIDRATACAO).pontosRecompensa(20)
                .bonusSaude(5).bonusHidratacao(15).bonusSono(0).bonusExercicio(0).bonusBemEstar(0)
                .ativa(true).build();
    }

    @Test
    @DisplayName("Deve buscar avatar do usuário com sucesso")
    void deveBuscarAvatarComSucesso() {
        when(avatarRepository.findByUsuarioId(1L)).thenReturn(Optional.of(avatar));

        AvatarResponseDTO resultado = avatarService.buscarPorUsuario(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNomeAvatar()).isEqualTo("AvatarJoao");
        assertThat(resultado.getNivel()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve aplicar bônus ao completar missão")
    void deveAplicarBonusAoCompletarMissao() {
        CompletarMissaoDTO dto = new CompletarMissaoDTO(1L, "Missão completada!");

        when(usuarioService.buscarEntidade(1L)).thenReturn(usuario);
        when(missaoService.buscarEntidade(1L)).thenReturn(missao);
        when(avatarRepository.findByUsuarioId(1L)).thenReturn(Optional.of(avatar));
        when(avatarRepository.save(any())).thenReturn(avatar);
        MissaoCompletada mc = MissaoCompletada.builder().id(1L).usuario(usuario)
                .missao(missao).observacao("ok").build();
        when(missaoCompletadaRepository.save(any())).thenReturn(mc);

        MissaoCompletadaResponseDTO resultado = avatarService.completarMissao(1L, dto);

        assertThat(resultado).isNotNull();
        assertThat(avatar.getPontosTotal()).isEqualTo(20);
        assertThat(avatar.getHidratacao()).isEqualTo(15);
        assertThat(avatar.getSaude()).isEqualTo(5);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar completar missão inativa")
    void deveLancarExcecaoMissaoInativa() {
        missao.setAtiva(false);
        CompletarMissaoDTO dto = new CompletarMissaoDTO(1L, null);

        when(usuarioService.buscarEntidade(1L)).thenReturn(usuario);
        when(missaoService.buscarEntidade(1L)).thenReturn(missao);
        // avatarRepository NÃO é chamado: a exceção é lançada antes de buscarAvatarPorUsuario

        assertThatThrownBy(() -> avatarService.completarMissao(1L, dto))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("não está disponível");
    }

    @Test
    @DisplayName("Deve fazer level up quando todas as barras atingem 100")
    void deveFazerLevelUpQuandoTodasBarrasAtingem100() {
        // Coloca avatar quase no limite
        avatar.setSaude(95); avatar.setHidratacao(95); avatar.setSono(95);
        avatar.setExercicio(95); avatar.setBemEstar(95);

        Missao missaoGrande = Missao.builder()
                .id(2L).titulo("Super Missão").descricao("Pontuação máxima")
                .categoria(CategoriaMissao.SAUDE).pontosRecompensa(50)
                .bonusSaude(10).bonusHidratacao(10).bonusSono(10).bonusExercicio(10).bonusBemEstar(10)
                .ativa(true).build();

        CompletarMissaoDTO dto = new CompletarMissaoDTO(2L, null);

        when(usuarioService.buscarEntidade(1L)).thenReturn(usuario);
        when(missaoService.buscarEntidade(2L)).thenReturn(missaoGrande);
        when(avatarRepository.findByUsuarioId(1L)).thenReturn(Optional.of(avatar));
        when(avatarRepository.save(any())).thenReturn(avatar);
        MissaoCompletada mc = MissaoCompletada.builder().id(1L).usuario(usuario).missao(missaoGrande).build();
        when(missaoCompletadaRepository.save(any())).thenReturn(mc);

        avatarService.completarMissao(1L, dto);

        assertThat(avatar.getNivel()).isEqualTo(2);
        assertThat(avatar.getSaude()).isEqualTo(0);
        assertThat(avatar.getPontosTotal()).isEqualTo(50);
    }
}
