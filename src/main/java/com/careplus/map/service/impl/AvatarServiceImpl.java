package com.careplus.map.service.impl;

import com.careplus.map.exception.RecursoNaoEncontradoException;
import com.careplus.map.exception.RegraNegocioException;
import com.careplus.map.model.dto.*;
import com.careplus.map.model.entity.*;
import com.careplus.map.model.vo.EstatisticasVO;
import com.careplus.map.model.vo.RankingVO;
import com.careplus.map.repository.*;
import com.careplus.map.service.IAvatarService;
import com.careplus.map.service.IMissaoService;
import com.careplus.map.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de avatar e gamificação.
 * Contém as regras de negócio de evolução do avatar ao completar missões.
 */
@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements IAvatarService {

    private final AvatarRepository avatarRepository;
    private final MissaoCompletadaRepository missaoCompletadaRepository;
    private final IUsuarioService usuarioService;
    private final IMissaoService missaoService;

    @Override
    @Transactional(readOnly = true)
    public AvatarResponseDTO buscarPorUsuario(Long usuarioId) {
        return toResponseDTO(buscarAvatarPorUsuario(usuarioId));
    }

    @Override
    @Transactional
    public MissaoCompletadaResponseDTO completarMissao(Long usuarioId, CompletarMissaoDTO dto) {
        Usuario usuario = usuarioService.buscarEntidade(usuarioId);
        Missao missao = missaoService.buscarEntidade(dto.getMissaoId());

        if (!missao.getAtiva()) {
            throw new RegraNegocioException(
                    "A missão '" + missao.getTitulo() + "' não está disponível no momento.");
        }

        Avatar avatar = buscarAvatarPorUsuario(usuarioId);
        aplicarRecompensas(avatar, missao);
        avatarRepository.save(avatar);

        MissaoCompletada registro = MissaoCompletada.builder()
                .usuario(usuario)
                .missao(missao)
                .observacao(dto.getObservacao())
                .build();
        MissaoCompletada salvo = missaoCompletadaRepository.save(registro);

        return MissaoCompletadaResponseDTO.builder()
                .id(salvo.getId())
                .usuarioId(usuarioId)
                .missaoId(missao.getId())
                .tituloMissao(missao.getTitulo())
                .observacao(salvo.getObservacao())
                .completadaEm(salvo.getCompletadaEm())
                .avatarAtualizado(toResponseDTO(avatar))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public EstatisticasVO buscarEstatisticas(Long usuarioId) {
        usuarioService.buscarEntidade(usuarioId);
        Avatar avatar = buscarAvatarPorUsuario(usuarioId);

        long totalMissoes = missaoCompletadaRepository.countByUsuarioId(usuarioId);

        List<Object[]> raw = missaoCompletadaRepository.countMissoesPorCategoria(usuarioId);
        Map<String, Long> porCategoria = new LinkedHashMap<>();
        for (Object[] row : raw) {
            porCategoria.put(row[0].toString(), (Long) row[1]);
        }

        int pontosProxNivel = (avatar.getNivel() + 1) * 100;
        int pontosParaSubir = Math.max(0, pontosProxNivel - avatar.getPontosTotal());

        return EstatisticasVO.builder()
                .totalMissoesCompletadas(totalMissoes)
                .missoesPorCategoria(porCategoria)
                .pontosTotal(avatar.getPontosTotal())
                .nivelAtual(avatar.getNivel())
                .pontosParaProximoNivel(pontosParaSubir)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RankingVO> ranking() {
        List<Avatar> avatares = avatarRepository.findRankingGlobal();
        AtomicInteger posicao = new AtomicInteger(1);

        return avatares.stream().map(a -> {
            long missoes = missaoCompletadaRepository.countByUsuarioId(a.getUsuario().getId());
            return RankingVO.builder()
                    .posicao(posicao.getAndIncrement())
                    .usuarioId(a.getUsuario().getId())
                    .nomeUsuario(a.getUsuario().getNome())
                    .nomeAvatar(a.getNomeAvatar())
                    .nivel(a.getNivel())
                    .pontosTotal(a.getPontosTotal())
                    .totalMissoesCompletadas(missoes)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * Aplica os pontos e bônus da missão ao avatar.
     * Regra de evolução: se todas as 5 barras atingem 100, o avatar sobe de nível.
     */
    private void aplicarRecompensas(Avatar avatar, Missao missao) {
        avatar.setPontosTotal(avatar.getPontosTotal() + missao.getPontosRecompensa());
        avatar.setSaude(avatar.getSaude() + missao.getBonusSaude());
        avatar.setHidratacao(avatar.getHidratacao() + missao.getBonusHidratacao());
        avatar.setSono(avatar.getSono() + missao.getBonusSono());
        avatar.setExercicio(avatar.getExercicio() + missao.getBonusExercicio());
        avatar.setBemEstar(avatar.getBemEstar() + missao.getBonusBemEstar());

        boolean levelUp = avatar.getSaude() >= 100 && avatar.getHidratacao() >= 100
                && avatar.getSono() >= 100 && avatar.getExercicio() >= 100
                && avatar.getBemEstar() >= 100;

        if (levelUp) {
            avatar.setNivel(avatar.getNivel() + 1);
            avatar.setSaude(0);
            avatar.setHidratacao(0);
            avatar.setSono(0);
            avatar.setExercicio(0);
            avatar.setBemEstar(0);
        } else {
            avatar.setSaude(Math.min(100, avatar.getSaude()));
            avatar.setHidratacao(Math.min(100, avatar.getHidratacao()));
            avatar.setSono(Math.min(100, avatar.getSono()));
            avatar.setExercicio(Math.min(100, avatar.getExercicio()));
            avatar.setBemEstar(Math.min(100, avatar.getBemEstar()));
        }
    }

    private Avatar buscarAvatarPorUsuario(Long usuarioId) {
        return avatarRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Avatar para o usuário com ID " + usuarioId + " não encontrado."));
    }

    public AvatarResponseDTO toResponseDTO(Avatar a) {
        return AvatarResponseDTO.builder()
                .id(a.getId())
                .usuarioId(a.getUsuario().getId())
                .nomeAvatar(a.getNomeAvatar())
                .nivel(a.getNivel())
                .pontosTotal(a.getPontosTotal())
                .saude(a.getSaude())
                .hidratacao(a.getHidratacao())
                .sono(a.getSono())
                .exercicio(a.getExercicio())
                .bemEstar(a.getBemEstar())
                .atualizadoEm(a.getAtualizadoEm())
                .build();
    }
}
