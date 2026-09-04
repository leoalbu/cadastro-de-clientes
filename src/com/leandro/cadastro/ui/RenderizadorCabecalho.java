package com.leandro.cadastro.ui;

import com.leandro.cadastro.ui.tema.Cores;
import com.leandro.cadastro.ui.tema.Fontes;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

/** Cabecalho da tabela: rotulo em maiusculas, cinza, com respiro lateral. */
public class RenderizadorCabecalho implements TableCellRenderer {

    private final TableCellRenderer base;

    public RenderizadorCabecalho(TableCellRenderer base) {
        this.base = base;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = base.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        if (c instanceof JLabel l) {
            l.setText(String.valueOf(value).toUpperCase());
            l.setFont(Fontes.semibold(11f));
            l.setForeground(Cores.TINTA_SUAVE);
            l.setBorder(new EmptyBorder(0, 14, 0, 14));
        }
        if (c instanceof JComponent jc) {
            jc.setBackground(Cores.SUPERFICIE_2);
        }
        return c;
    }
}
