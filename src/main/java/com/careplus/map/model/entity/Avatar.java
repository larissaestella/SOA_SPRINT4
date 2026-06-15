package com.careplus.map.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidade que representa o avatar digital do usuário na plataforma MAP.
 *
 * O avatar é o núcleo da gamificação: evolui conforme o usuário completa missões de prevenção.
 */
@Entity
@Table(name = "avatares")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "nome_avatar", nullable = false, length = 80)
    private String nomeAvatar;

    @Column(nullable = false)
    @Builder.Default
    private Integer nivel = 1;

    @Column(name = "pontos_total", nullable = false)
    @Builder.Default
    private Integer pontosTotal = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer saude = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer hidratacao = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer sono = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer exercicio = 0;

    @Column(name = "bem_estar", nullable = false)
    @Builder.Default
    private Integer bemEstar = 0;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}