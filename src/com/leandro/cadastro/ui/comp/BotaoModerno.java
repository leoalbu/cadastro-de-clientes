package com.leandro.cadastro.ui.comp;

import com.leandro.cadastro.ui.tema.Cores;
import com.leandro.cadastro.ui.tema.Fontes;

import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Botao chato (flat) com cantos arredondados e tres variantes. */
public class BotaoModerno extends JButton {

    public enum Tipo { PRIMARIO, SECUNDARIO, GHOST }

    private final Tipo tipo;
    private boolean hover;
    private boolean pressionado;

    public BotaoModerno(String texto, Icon icone, Tipo tipo) {
        super(texto, icone);
        this.tipo = tipo;
        setFont(Fontes.semibold(13f));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setIconTextGap(8);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(9, 16, 9, 16));
        atualizarCorTexto();

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hover = false; pressionado = false; repaint(); }
            @Override public void mousePressed(MouseEvent e)  { pressionado = true; repaint(); }
            @Override public void mouseReleased(MouseEvent e) { pressionado = false; repaint(); }
        });
    }

    private void atualizarCorTexto() {
        switch (tipo) {
            case PRIMARIO -> setForeground(Color.WHITE);
            case SECUNDARIO -> setForeground(Cores.TINTA);
            case GHOST -> setForeground(Cores.TINTA_SUAVE);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.height = Math.max(d.height, 38);
        d.width += 12;
        return d;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight(), arco = 10;

        if (!isEnabled()) {
            g.setColor(Cores.SUPERFICIE_2);
            g.fillRoundRect(0, 0, w, h, arco, arco);
            g.setColor(Cores.BORDA);
            g.drawRoundRect(0, 0, w - 1, h - 1, arco, arco);
            g.dispose();
            super.paintComponent(g0);
            return;
        }

        Color fundo = null, borda = null;
        switch (tipo) {
            case PRIMARIO -> fundo = pressionado ? Cores.ACENTO_HOVER
                    : (hover ? Cores.ACENTO_HOVER : Cores.ACENTO);
            case SECUNDARIO -> {
                fundo = pressionado ? Cores.SUPERFICIE_2 : (hover ? Cores.SUPERFICIE_2 : Cores.SUPERFICIE);
                borda = Cores.BORDA_FORTE;
            }
            case GHOST -> {
                if (hover || pressionado) fundo = Cores.SUPERFICIE_2;
            }
        }

        if (fundo != null) {
            g.setColor(fundo);
            g.fillRoundRect(0, 0, w, h, arco, arco);
        }
        if (borda != null) {
            g.setColor(borda);
            g.drawRoundRect(0, 0, w - 1, h - 1, arco, arco);
        }
        g.dispose();
        super.paintComponent(g0);
    }
}
