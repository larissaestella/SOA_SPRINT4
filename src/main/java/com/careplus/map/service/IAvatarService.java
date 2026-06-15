package com.careplus.map.service;

import com.careplus.map.model.dto.AvatarResponseDTO;
import com.careplus.map.model.dto.MissaoCompletadaResponseDTO;
import com.careplus.map.model.dto.CompletarMissaoDTO;
import com.careplus.map.model.vo.EstatisticasVO;
import com.careplus.map.model.vo.RankingVO;

import java.util.List;

/**
 * Contrato de serviço para operações do avatar e gamificação.
 */
public interface IAvatarService {

    AvatarResponseDTO buscarPorUsuario(Long usuarioId);

    MissaoCompletadaResponseDTO completarMissao(Long usuarioId, CompletarMissaoDTO dto);

    EstatisticasVO buscarEstatisticas(Long usuarioId);

    List<RankingVO> ranking();
}
