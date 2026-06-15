package com.careplus.map.controller;

import com.careplus.map.model.dto.MissaoCadastroDTO;
import com.careplus.map.model.dto.MissaoResponseDTO;
import com.careplus.map.model.enums.CategoriaMissao;
import com.careplus.map.service.IMissaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/missoes")
@RequiredArgsConstructor
@Tag(name = "Missões", description = "Gerenciamento de missões preventivas da plataforma MAP")
@SecurityRequirement(name = "bearerAuth")
public class MissaoController {

    private final IMissaoService missaoService;

    @PostMapping
    @Operation(summary = "Criar missão (ADMIN)", description = "Cadastra uma nova missão preventiva")
    public ResponseEntity<MissaoResponseDTO> criar(@Valid @RequestBody MissaoCadastroDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(missaoService.criar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar missões ativas", description = "Retorna missões ativas, com filtro opcional por categoria")
    public ResponseEntity<List<MissaoResponseDTO>> listar(
            @RequestParam(required = false) CategoriaMissao categoria) {
        return ResponseEntity.ok(missaoService.listar(categoria));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar missão por ID")
    public ResponseEntity<MissaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(missaoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar missão (ADMIN)")
    public ResponseEntity<MissaoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody MissaoCadastroDTO dto) {
        return ResponseEntity.ok(missaoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar missão (ADMIN)", description = "Soft-delete: a missão fica inativa mas não é removida do banco")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        missaoService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
