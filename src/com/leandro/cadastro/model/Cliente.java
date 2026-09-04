package com.leandro.cadastro.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Entidade principal do sistema. Guarda os dados cadastrais do cliente e um
 * unico saldo a receber com data de vencimento.
 *
 * <p>O {@code id} e atribuido pelo repositorio ao salvar um cliente novo; ate
 * la fica {@code null}. Igualdade e baseada apenas no id.</p>
 */
public class Cliente {

    private Long id;
    private String nome;
    private String cpfCnpj;
    private String email;
    private String telefone;
    private Endereco endereco = new Endereco();
    private LocalDate dataCadastro = LocalDate.now();
    private StatusCliente status = StatusCliente.ATIVO;
    private String observacoes;

    private BigDecimal valorAReceber = BigDecimal.ZERO;
    private LocalDate dataVencimento;

    public Cliente() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco == null ? new Endereco() : endereco;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public StatusCliente getStatus() {
        return status;
    }

    public void setStatus(StatusCliente status) {
        this.status = status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public BigDecimal getValorAReceber() {
        return valorAReceber == null ? BigDecimal.ZERO : valorAReceber;
    }

    public void setValorAReceber(BigDecimal valorAReceber) {
        this.valorAReceber = valorAReceber == null ? BigDecimal.ZERO : valorAReceber;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    /** {@code true} se ha valor a receber maior que zero. */
    public boolean temValorAReceber() {
        return getValorAReceber().compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Calcula a situacao financeira do cliente em relacao a data de referencia.
     *
     * @param referencia normalmente {@code LocalDate.now()}
     */
    public SituacaoFinanceira getSituacaoFinanceira(LocalDate referencia) {
        if (!temValorAReceber() || dataVencimento == null) {
            return SituacaoFinanceira.EM_DIA;
        }
        if (dataVencimento.isBefore(referencia)) {
            return SituacaoFinanceira.VENCIDO;
        }
        if (dataVencimento.isEqual(referencia)) {
            return SituacaoFinanceira.VENCE_HOJE;
        }
        return SituacaoFinanceira.A_VENCER;
    }

    /**
     * Dias a partir de {@code referencia} ate o vencimento. Negativo se ja
     * venceu. {@code null} se nao ha vencimento ou valor a receber.
     */
    public Long diasAteVencimento(LocalDate referencia) {
        if (!temValorAReceber() || dataVencimento == null) {
            return null;
        }
        return ChronoUnit.DAYS.between(referencia, dataVencimento);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente other = (Cliente) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return nome + (cpfCnpj != null ? " (" + cpfCnpj + ")" : "");
    }
}
