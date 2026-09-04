package com.leandro.cadastro.ui;

import com.leandro.cadastro.model.Cliente;
import com.leandro.cadastro.util.Formatos;
import com.leandro.cadastro.util.ValidadorCpfCnpj;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Adapta uma lista de {@link Cliente} para a {@link javax.swing.JTable}. */
public class ClienteTableModel extends AbstractTableModel {

    private static final String[] COLUNAS = {
            "Nome", "CPF/CNPJ", "Telefone", "Cidade/UF",
            "Valor a Receber", "Vencimento", "Situação", "Status"
    };

    private List<Cliente> clientes = new ArrayList<>();

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes == null ? new ArrayList<>() : clientes;
        fireTableDataChanged();
    }

    public Cliente getClienteEm(int linha) {
        return clientes.get(linha);
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    @Override
    public int getRowCount() {
        return clientes.size();
    }

    @Override
    public int getColumnCount() {
        return COLUNAS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUNAS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Cliente c = clientes.get(rowIndex);
        LocalDate hoje = LocalDate.now();
        return switch (columnIndex) {
            case 0 -> c.getNome();
            case 1 -> ValidadorCpfCnpj.formatar(c.getCpfCnpj());
            case 2 -> c.getTelefone() == null ? "" : c.getTelefone();
            case 3 -> c.getEndereco().getCidadeUf();
            case 4 -> Formatos.moeda(c.getValorAReceber());
            case 5 -> Formatos.data(c.getDataVencimento());
            case 6 -> c.getSituacaoFinanceira(hoje).getDescricao();
            case 7 -> c.getStatus().getDescricao();
            default -> "";
        };
    }
}
