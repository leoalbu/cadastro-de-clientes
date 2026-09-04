package com.leandro.cadastro.ui.comp;

import com.leandro.cadastro.ui.tema.Cores;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

/** Painel branco com cantos arredondados e borda sutil (estilo "card"). */
public class PainelCard extends JPanel {

    private final int arco;

    public PainelCard(LayoutManager layout) {
        this(layout, 14, 16, 16, 16, 16);
    }

    public PainelCard(LayoutManager layout, int arco, int t, int l, int b, int r) {
        super(layout);
        this.arco = arco;
        setOpaque(false);
        setBorder(new EmptyBorder(t, l, b, r));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Cores.SUPERFICIE);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), arco, arco);
        g.setColor(Cores.BORDA);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arco, arco);
        g.dispose();
        super.paintComponent(g0);
    }
}
