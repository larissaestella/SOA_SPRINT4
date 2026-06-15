package com.careplus.map.model.enums;

/**
 * categorias disponíveis para classificação das missões preventivas.
 */
public enum CategoriaMissao {
    SAUDE("Saúde"),
    HIDRATACAO("Hidratação"),
    SONO("Sono"),
    EXERCICIO("Exercício"),
    BEM_ESTAR("Bem-estar");

    private final String descricao;

    CategoriaMissao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
