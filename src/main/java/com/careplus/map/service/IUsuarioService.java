package com.careplus.map.service;

import com.careplus.map.model.dto.UsuarioAtualizacaoDTO;
import com.careplus.map.model.dto.UsuarioResponseDTO;
import com.careplus.map.model.entity.Usuario;

import java.util.List;

/**
 * Contrato de serviço para operações de gerenciamento de usuários.
 * Permite extensibilidade e facilita testes com mocks.
 */
public interface IUsuarioService {

    UsuarioResponseDTO buscarPorId(Long id);

    List<UsuarioResponseDTO> listar();

    UsuarioResponseDTO atualizar(Long id, UsuarioAtualizacaoDTO dto);

    void remover(Long id);

    Usuario buscarEntidade(Long id);
}
