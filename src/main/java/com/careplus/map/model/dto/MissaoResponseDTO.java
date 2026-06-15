package com.careplus.map.model.dto;

import com.careplus.map.model.enums.CategoriaMissao;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissaoResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private CategoriaMissao categoria;
    private Integer pontosRecompensa;
    private Integer bonusSaude;
    private Integer bonusHidratacao;
    private Integer bonusSono;
    private Integer bonusExercicio;
    private Integer bonusBemEstar;
    private Boolean ativa;
}