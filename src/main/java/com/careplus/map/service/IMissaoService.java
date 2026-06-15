package com.careplus.map.service;

import com.careplus.map.model.dto.MissaoCadastroDTO;
import com.careplus.map.model.dto.MissaoResponseDTO;
import com.careplus.map.model.entity.Missao;
import com.careplus.map.model.enums.CategoriaMissao;

import java.util.List;

/**
 * Contrato de serviço para operações de missões.
 */
public interface IMissaoService {

    MissaoResponseDTO criar(MissaoCadastroDTO dto);

    List<MissaoResponseDTO> listar(CategoriaMissao categoria);

    MissaoResponseDTO buscarPorId(Long id);

    MissaoResponseDTO atualizar(Long id, MissaoCadastroDTO dto);

    void desativar(Long id);

    Missao buscarEntidade(Long id);
}
