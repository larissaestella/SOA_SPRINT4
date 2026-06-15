package com.careplus.map.service.impl;

import com.careplus.map.exception.RecursoNaoEncontradoException;
import com.careplus.map.model.dto.MissaoCadastroDTO;
import com.careplus.map.model.dto.MissaoResponseDTO;
import com.careplus.map.model.entity.Missao;
import com.careplus.map.model.enums.CategoriaMissao;
import com.careplus.map.repository.MissaoRepository;
import com.careplus.map.service.IMissaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementação do serviço de missões.
 * Encapsula a lógica de criação, listagem, atualização e desativação de missões.
 */
@Service
@RequiredArgsConstructor
public class MissaoServiceImpl implements IMissaoService {

    private final MissaoRepository missaoRepository;

    @Override
    @Transactional
    public MissaoResponseDTO criar(MissaoCadastroDTO dto) {
        Missao missao = Missao.builder()
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .categoria(dto.getCategoria())
                .pontosRecompensa(dto.getPontosRecompensa())
                .bonusSaude(nullOr(dto.getBonusSaude()))
                .bonusHidratacao(nullOr(dto.getBonusHidratacao()))
                .bonusSono(nullOr(dto.getBonusSono()))
                .bonusExercicio(nullOr(dto.getBonusExercicio()))
                .bonusBemEstar(nullOr(dto.getBonusBemEstar()))
                .build();

        return toResponseDTO(missaoRepository.save(missao));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MissaoResponseDTO> listar(CategoriaMissao categoria) {
        List<Missao> missoes = (categoria != null)
                ? missaoRepository.findByAtivaAndCategoria(true, categoria)
                : missaoRepository.findByAtivaTrue();

        return missoes.stream().map(this::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MissaoResponseDTO buscarPorId(Long id) {
        return toResponseDTO(buscarEntidade(id));
    }

    @Override
    @Transactional
    public MissaoResponseDTO atualizar(Long id, MissaoCadastroDTO dto) {
        Missao missao = buscarEntidade(id);
        missao.setTitulo(dto.getTitulo());
        missao.setDescricao(dto.getDescricao());
        missao.setCategoria(dto.getCategoria());
        missao.setPontosRecompensa(dto.getPontosRecompensa());
        missao.setBonusSaude(nullOr(dto.getBonusSaude()));
        missao.setBonusHidratacao(nullOr(dto.getBonusHidratacao()));
        missao.setBonusSono(nullOr(dto.getBonusSono()));
        missao.setBonusExercicio(nullOr(dto.getBonusExercicio()));
        missao.setBonusBemEstar(nullOr(dto.getBonusBemEstar()));
        return toResponseDTO(missaoRepository.save(missao));
    }

    @Override
    @Transactional
    public void desativar(Long id) {
        Missao missao = buscarEntidade(id);
        missao.setAtiva(false);
        missaoRepository.save(missao);
    }

    @Override
    public Missao buscarEntidade(Long id) {
        return missaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Missão", id));
    }

    public MissaoResponseDTO toResponseDTO(Missao m) {
        return MissaoResponseDTO.builder()
                .id(m.getId())
                .titulo(m.getTitulo())
                .descricao(m.getDescricao())
                .categoria(m.getCategoria())
                .pontosRecompensa(m.getPontosRecompensa())
                .bonusSaude(m.getBonusSaude())
                .bonusHidratacao(m.getBonusHidratacao())
                .bonusSono(m.getBonusSono())
                .bonusExercicio(m.getBonusExercicio())
                .bonusBemEstar(m.getBonusBemEstar())
                .ativa(m.getAtiva())
                .build();
    }

    private int nullOr(Integer value) {
        return value != null ? value : 0;
    }
}
