package com.careplus.map.model.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de autenticação com token JWT")
public class LoginResponseDTO {

    @Schema(description = "Token JWT para uso nas requisições autenticadas")
    private String token;

    @Schema(description = "Tipo do token", example = "Bearer")
    private String tipo;

    @Schema(description = "E-mail do usuário autenticado")
    private String email;

    @Schema(description = "Perfil do usuário autenticado")
    private String perfil;
}
