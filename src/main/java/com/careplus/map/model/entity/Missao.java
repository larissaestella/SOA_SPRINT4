package com.careplus.map.model.entity;

import com.careplus.map.model.enums.CategoriaMissao;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Missões são ações de autocuidado que o usuário pode completar para evoluir seu avatar.
 */
@Entity
@Table(name = "missoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Missao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CategoriaMissao categoria;

    @Column(name = "pontos_recompensa", nullable = false)
    @Builder.Default
    private Integer pontosRecompensa = 10;

    @Column(name = "bonus_saude", nullable = false)
    @Builder.Default
    private Integer bonusSaude = 0;

    @Column(name = "bonus_hidratacao", nullable = false)
    @Builder.Default
    private Integer bonusHidratacao = 0;

    @Column(name = "bonus_sono", nullable = false)
    @Builder.Default
    private Integer bonusSono = 0;

    @Column(name = "bonus_exercicio", nullable = false)
    @Builder.Default
    private Integer bonusExercicio = 0;

    @Column(name = "bonus_bem_estar", nullable = false)
    @Builder.Default
    private Integer bonusBemEstar = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativa = true;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
    }
}