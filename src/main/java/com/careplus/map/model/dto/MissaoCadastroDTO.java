package com.careplus.map.model.dto;

import com.careplus.map.model.enums.CategoriaMissao;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissaoCadastroDTO {

    @NotBlank(message = "O título é obrigatório")
    @Size(min = 3, max = 120, message = "O título deve ter entre 3 e 120 caracteres")
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(min = 10, max = 500, message = "A descrição deve ter entre 10 e 500 caracteres")
    private String descricao;

    @NotNull(message = "A categoria é obrigatória")
    private CategoriaMissao categoria;

    @NotNull(message = "Os pontos de recompensa são obrigatórios")
    @Min(value = 1, message = "A recompensa mínima é de 1 ponto")
    @Max(value = 500, message = "A recompensa máxima é de 500 pontos")
    private Integer pontosRecompensa;

    @Min(value = 0, message = "O bônus não pode ser negativo") @Max(value = 100)
    private Integer bonusSaude = 0;

    @Min(value = 0, message = "O bônus não pode ser negativo") @Max(value = 100)
    private Integer bonusHidratacao = 0;

    @Min(value = 0, message = "O bônus não pode ser negativo") @Max(value = 100)
    private Integer bonusSono = 0;

    @Min(value = 0, message = "O bônus não pode ser negativo") @Max(value = 100)
    private Integer bonusExercicio = 0;

    @Min(value = 0, message = "O bônus não pode ser negativo") @Max(value = 100)
    private Integer bonusBemEstar = 0;
}