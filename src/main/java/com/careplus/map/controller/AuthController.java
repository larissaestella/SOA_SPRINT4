package com.careplus.map.controller;

import com.careplus.map.model.dto.UsuarioResponseDTO;
import com.careplus.map.model.dto.auth.LoginRequestDTO;
import com.careplus.map.model.dto.auth.LoginResponseDTO;
import com.careplus.map.model.dto.auth.RegisterRequestDTO;
import com.careplus.map.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints públicos de autenticação (registro e login).
 * Não exigem token JWT.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Registro e login de usuários")
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/registrar")
    @Operation(
            summary = "Registrar novo usuário",
            description = "Cria uma conta de usuário com perfil USUARIO e gera o avatar inicial",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                            content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos ou e-mail já cadastrado")
            }
    )
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(dto));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Autenticar usuário",
            description = "Autentica com e-mail e senha, retorna token JWT para uso nas demais requisições",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso",
                            content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
            }
    )
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.autenticar(dto));
    }
}
