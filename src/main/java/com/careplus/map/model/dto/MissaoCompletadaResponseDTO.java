package com.careplus.map.model.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissaoCompletadaResponseDTO {
    private Long id;
    private Long usuarioId;
    private Long missaoId;
    private String tituloMissao;
    private String observacao;
    private LocalDateTime completadaEm;
    private AvatarResponseDTO avatarAtualizado;
}