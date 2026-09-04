package com.leandro.cadastro.model;

/**
 * Situacao do valor a receber de um cliente. Nao e armazenada: e calculada
 * a partir do valor a receber e da data de vencimento (ver
 * {@link Cliente#getSituacaoFinanceira(java.time.LocalDate)}).
 */
public enum SituacaoFinanceira {
    EM_DIA("Em dia"),
    A_VENCER("A vencer"),
    VENCE_HOJE("Vence hoje"),
    VENCIDO("Vencido");

    private final String descricao;

    SituacaoFinanceira(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
