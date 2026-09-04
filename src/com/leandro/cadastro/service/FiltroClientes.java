package com.leandro.cadastro.service;

import com.leandro.cadastro.model.SituacaoFinanceira;
import com.leandro.cadastro.model.StatusCliente;

/**
 * Criterios de busca. Todo campo {@code null} significa "nao filtrar por isso".
 * Usado por {@link ClienteService#filtrar(FiltroClientes)}.
 */
public class FiltroClientes {

    /** Texto procurado no nome ou no CPF/CNPJ (parcial, ignora maiusculas). */
    private String texto;

    /** Se preenchido, so clientes com este status. */
    private StatusCliente status;

    /** Se preenchido, so clientes nesta situacao financeira. */
    private SituacaoFinanceira situacao;

    /**
     * Se preenchido, so clientes com valor a receber que vencem entre hoje e
     * hoje + N dias (inclusive). Nao inclui vencidos.
     */
    private Integer venceEmDias;

    public String getTexto() {
        return texto;
    }

    public FiltroClientes setTexto(String texto) {
        this.texto = texto == null || texto.isBlank() ? null : texto.trim();
        return this;
    }

    public StatusCliente getStatus() {
        return status;
    }

    public FiltroClientes setStatus(StatusCliente status) {
        this.status = status;
        return this;
    }

    public SituacaoFinanceira getSituacao() {
        return situacao;
    }

    public FiltroClientes setSituacao(SituacaoFinanceira situacao) {
        this.situacao = situacao;
        return this;
    }

    public Integer getVenceEmDias() {
        return venceEmDias;
    }

    public FiltroClientes setVenceEmDias(Integer venceEmDias) {
        this.venceEmDias = venceEmDias;
        return this;
    }

    public boolean vazio() {
        return texto == null && status == null && situacao == null && venceEmDias == null;
    }
}
