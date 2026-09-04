package com.leandro.cadastro.ui;

import com.leandro.cadastro.model.Cliente;
import com.leandro.cadastro.model.SituacaoFinanceira;
import com.leandro.cadastro.model.StatusCliente;
import com.leandro.cadastro.ui.tema.Cores;
import com.leandro.cadastro.ui.tema.Fontes;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.time.LocalDate;

/**
 * Renderizacao da tabela de clientes no estilo atual: linhas zebradas, selecao
 * em tom do acento, faixa colorida a esquerda para vencidos/proximos e "pills"
 * (badges) nas colunas de situacao e status.
 */
public final class RenderizadorTabela {

    private RenderizadorTabela() {
    }

    public static void aplicar(JTable tabela, ClienteTableModel model) {
        tabela.setRowHeight(38);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionBackground(Cores.SELECAO);
        tabela.setSelectionForeground(Cores.TINTA);
        tabela.getTableHeader().setReorderingAllowed(false);

        CelulaPadrao padrao = new CelulaPadrao(model);
        CelulaBadge badge = new CelulaBadge(model);
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i)
                    .setCellRenderer(i == 6 || i == 7 ? badge : padrao);
        }
        // valor a receber alinhado a direita
        tabela.getColumnModel().getColumn(4).setCellRenderer(new CelulaPadrao(model) {
            {
                setHorizontalAlignment(SwingConstants.RIGHT);
            }
        });
    }

    private static Cliente clienteDaLinha(JTable t, ClienteTableModel m, int row) {
        return m.getClienteEm(t.convertRowIndexToModel(row));
    }

    private static Color fundoLinha(Cliente c, boolean selecionado, int row) {
        if (selecionado) {
            return Cores.SELECAO;
        }
        return row % 2 == 0 ? Cores.SUPERFICIE : Cores.LINHA_PAR;
    }

    private static SituacaoFinanceira situacao(Cliente c) {
        return c.getSituacaoFinanceira(LocalDate.now());
    }

    // ------------------------------------------------------------- padrao

    private static class CelulaPadrao extends DefaultTableCellRenderer {
        private final ClienteTableModel model;
        private Color faixa;

        CelulaPadrao(ClienteTableModel model) {
            this.model = model;
            setBorder(new EmptyBorder(0, 14, 0, 14));
            setFont(Fontes.regular(13f));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Cliente c = clienteDaLinha(table, model, row);
            SituacaoFinanceira sit = situacao(c);
            Long dias = c.diasAteVencimento(LocalDate.now());

            setBackground(fundoLinha(c, isSelected, row));
            boolean inativo = c.getStatus() == StatusCliente.INATIVO;
            setForeground(inativo ? Cores.TINTA_FRACA : Cores.TINTA);

            faixa = null;
            if (column == 0) {
                if (sit == SituacaoFinanceira.VENCIDO) faixa = Cores.VENCIDO;
                else if (sit == SituacaoFinanceira.VENCE_HOJE
                        || (dias != null && dias >= 0 && dias <= 7)) faixa = Cores.PROXIMO;
            }
            setBorder(new EmptyBorder(0, column == 0 ? 16 : 14, 0, 14));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (faixa != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(faixa);
                g2.fillRect(0, 6, 3, getHeight() - 12);
                g2.dispose();
            }
        }
    }

    // -------------------------------------------------------------- badge

    private static class CelulaBadge extends DefaultTableCellRenderer {
        private final ClienteTableModel model;
        private Color corTexto = Cores.TINTA;
        private Color corFundo = Cores.SUPERFICIE_2;
        private Color fundoCelula = Cores.SUPERFICIE;
        private String rotulo = "";

        CelulaBadge(ClienteTableModel model) {
            this.model = model;
            setFont(Fontes.semibold(11.5f));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Cliente c = clienteDaLinha(table, model, row);
            fundoCelula = fundoLinha(c, isSelected, row);
            rotulo = String.valueOf(value);

            if (column == 6) {
                switch (situacao(c)) {
                    case VENCIDO      -> cores(Cores.VENCIDO, Cores.VENCIDO_BG);
                    case VENCE_HOJE   -> cores(Cores.PROXIMO, Cores.PROXIMO_BG);
                    case A_VENCER     -> {
                        Long d = c.diasAteVencimento(LocalDate.now());
                        if (d != null && d <= 7) cores(Cores.PROXIMO, Cores.PROXIMO_BG);
                        else cores(Cores.OK, Cores.OK_BG);
                    }
                    default           -> cores(Cores.OK, Cores.OK_BG);
                }
            } else {
                if (c.getStatus() == StatusCliente.ATIVO) cores(Cores.ACENTO, Cores.ACENTO_SUAVE);
                else cores(Cores.INATIVO, Cores.INATIVO_BG);
            }
            return this;
        }

        private void cores(Color texto, Color fundo) {
            this.corTexto = texto;
            this.corFundo = fundo;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(fundoCelula);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setFont(getFont());
            int textoW = g.getFontMetrics().stringWidth(rotulo);
            int padH = 10, padV = 5;
            int bw = textoW + padH * 2;
            int bh = g.getFontMetrics().getHeight() + padV;
            int x = 14, y = (getHeight() - bh) / 2;

            g.setColor(corFundo);
            g.fillRoundRect(x, y, bw, bh, bh, bh);
            g.setColor(corTexto);
            g.drawString(rotulo, x + padH,
                    y + bh / 2 + g.getFontMetrics().getAscent() / 2 - 2);
            g.dispose();
        }
    }
}
