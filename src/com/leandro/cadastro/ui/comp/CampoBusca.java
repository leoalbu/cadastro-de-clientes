package com.leandro.cadastro.ui.comp;

import com.leandro.cadastro.ui.tema.Cores;
import com.leandro.cadastro.ui.tema.Fontes;
import com.leandro.cadastro.ui.tema.Icones;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Campo de texto arredondado com icone de lupa e placeholder. */
public class CampoBusca extends JTextField {

    private final String placeholder;

    public CampoBusca(String placeholder, int colunas) {
        super(colunas);
        this.placeholder = placeholder;
        setFont(Fontes.regular(13f));
        setOpaque(false);
        setForeground(Cores.TINTA);
        setCaretColor(Cores.ACENTO);
        setBorder(new EmptyBorder(8, 34, 8, 12));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();

        g.setColor(Cores.SUPERFICIE);
        g.fillRoundRect(0, 0, w, h, 9, 9);
        g.setColor(isFocusOwner() ? Cores.ACENTO : Cores.BORDA_FORTE);
        g.drawRoundRect(0, 0, w - 1, h - 1, 9, 9);

        Icones.de(Icones.LUPA, 16, Cores.TINTA_FRACA).paintIcon(this, g, 10, (h - 16) / 2);

        if (getText().isEmpty() && !isFocusOwner()) {
            g.setColor(Cores.TINTA_FRACA);
            g.setFont(getFont());
            g.drawString(placeholder, 34, h / 2 + g.getFontMetrics().getAscent() / 2 - 2);
        }
        g.dispose();
        super.paintComponent(g0);
    }
}
