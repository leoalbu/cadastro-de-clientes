package com.leandro.cadastro.service;

import com.leandro.cadastro.model.Cliente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Totais calculados sobre uma lista de clientes (normalmente a lista ja
 * filtrada exibida na tabela). Mostrado no rodape da janela.
 */
public class ResumoFinanceiro {

    private final int quantidadeClientes;
    private final BigDecimal totalAReceber;
    private final BigDecimal totalVencido;
    private final long quantidadeVencidos;

    private ResumoFinanceiro(int quantidadeClientes, BigDecimal totalAReceber,
                             BigDecimal totalVencido, long quantidadeVencidos) {
        this.quantidadeClientes = quantidadeClientes;
        this.totalAReceber = totalAReceber;
        this.totalVencido = totalVencido;
        this.quantidadeVencidos = quantidadeVencidos;
    }

    public static ResumoFinanceiro de(List<Cliente> clientes, LocalDate referencia) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal vencido = BigDecimal.ZERO;
        long qtdVencidos = 0;
        for (Cliente c : clientes) {
            total = total.add(c.getValorAReceber());
            switch (c.getSituacaoFinanceira(referencia)) {
                case VENCIDO -> {
                    vencido = vencido.add(c.getValorAReceber());
                    qtdVencidos++;
                }
                default -> {
                }
            }
        }
        return new ResumoFinanceiro(clientes.size(), total, vencido, qtdVencidos);
    }

    public int getQuantidadeClientes() {
        return quantidadeClientes;
    }

    public BigDecimal getTotalAReceber() {
        return totalAReceber;
    }

    public BigDecimal getTotalVencido() {
        return totalVencido;
    }

    public long getQuantidadeVencidos() {
        return quantidadeVencidos;
    }
}
