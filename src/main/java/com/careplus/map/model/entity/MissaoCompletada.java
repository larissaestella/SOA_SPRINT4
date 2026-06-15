package com.careplus.map.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Funciona como tabela de junção entre Usuario e Missao com dados extras.
 */
@Entity
@Table(name = "missoes_completadas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissaoCompletada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missao_id", nullable = false)
    private Missao missao;

    @Column(name = "completada_em", nullable = false, updatable = false)
    private LocalDateTime completadaEm;

    @Column(length = 300)
    private String observacao;

    @PrePersist
    protected void onCreate() {
        completadaEm = LocalDateTime.now();
    }
}
