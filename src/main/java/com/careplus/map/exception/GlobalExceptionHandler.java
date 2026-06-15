package com.careplus.map.exception;

import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // não encontrado (404)
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErroResponse.of(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    // regra de negócio violada (422)
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> handleRegraNegocio(RegraNegocioException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErroResponse.of(HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage()));
    }

    // credenciais inválidas (401)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErroResponse.of(HttpStatus.UNAUTHORIZED.value(), "E-mail ou senha inválidos."));
    }


    // validação de campos (@Valid) (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            erros.put(fe.getField(), fe.getDefaultMessage());
        }
        ErroResponse response = ErroResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erro("Erro de validação")
                .mensagem("Um ou mais campos são inválidos.")
                .campos(erros)
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    // acesso negado por @PreAuthorize (403) — usuário autenticado sem permissão
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErroResponse.of(HttpStatus.FORBIDDEN.value(), "Acesso negado: perfil insuficiente."));
    }

    // erros genéricos (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGenerico(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErroResponse.of(500, "Erro interno. Contate o suporte."));
    }

    // modelo de resposta de erro
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ErroResponse {
        private LocalDateTime timestamp;
        private Integer status;
        private String erro;
        private String mensagem;
        private Map<String, String> campos;

        public static ErroResponse of(int status, String mensagem) {
            String erro = HttpStatus.resolve(status) != null
                    ? HttpStatus.resolve(status).getReasonPhrase()
                    : "Erro";
            return ErroResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(status)
                    .erro(erro)
                    .mensagem(mensagem)
                    .build();
        }
    }
}