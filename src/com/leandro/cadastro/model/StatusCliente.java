package com.leandro.cadastro.model;

/** Situacao cadastral do cliente. */
public enum StatusCliente {
    ATIVO("Ativo"),
    INATIVO("Inativo");

    private final String descricao;

    StatusCliente(String descricao) {
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
