package com.careplus.map.service.impl;

import com.careplus.map.exception.RecursoNaoEncontradoException;
import com.careplus.map.exception.RegraNegocioException;
import com.careplus.map.model.dto.*;
import com.careplus.map.model.entity.Usuario;
import com.careplus.map.repository.UsuarioRepository;
import com.careplus.map.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementação do serviço de gerenciamento de usuários.
 * Encapsula toda a lógica de negócio relacionada a usuarios.
 */
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        return toResponseDTO(buscarEntidade(id));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioAtualizacaoDTO dto) {
        Usuario usuario = buscarEntidade(id);

        if (dto.getEmail() != null && !dto.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new RegraNegocioException("Já existe um usuário com o e-mail: " + dto.getEmail());
            }
            usuario.setEmail(dto.getEmail());
        }

        if (dto.getNome() != null)           usuario.setNome(dto.getNome());
        if (dto.getDataNascimento() != null)  usuario.setDataNascimento(dto.getDataNascimento());

        return toResponseDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void remover(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuarioRepository.delete(usuario);
    }

    @Override
    public Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }

    private UsuarioResponseDTO toResponseDTO(Usuario u) {
        return UsuarioResponseDTO.builder()
                .id(u.getId())
                .nome(u.getNome())
                .email(u.getEmail())
                .dataNascimento(u.getDataNascimento())
                .criadoEm(u.getCriadoEm())
                .build();
    }
}
