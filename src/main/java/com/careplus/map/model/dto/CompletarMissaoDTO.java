package com.careplus.map.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para completar uma missão")
public class CompletarMissaoDTO {

    @NotNull(message = "ID da missão é obrigatório")
    @Schema(description = "ID da missão a ser completada", example = "1")
    private Long missaoId;

    @Size(max = 300, message = "A observação pode ter no máximo 300 caracteres")
    @Schema(description = "Observação opcional sobre a execução da missão", example = "Completei correndo 5km")
    private String observacao;
}
