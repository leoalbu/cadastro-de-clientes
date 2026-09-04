package com.leandro.cadastro.ui.tema;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

/**
 * Icones desenhados com Java2D (sem arquivos, sem dependencias). Todos com
 * traco de 1.6px, cantos arredondados, no estilo "line icon" atual.
 */
public final class Icones {

    private Icones() {
    }

    public interface Desenho {
        void pintar(Graphics2D g, int tam, Color cor);
    }

    public static Icon de(Desenho desenho, int tamanho, Color cor) {
        return new IconeVetor(desenho, tamanho, cor);
    }

    private static final class IconeVetor implements Icon {
        private final Desenho desenho;
        private final int tamanho;
        private final Color cor;

        IconeVetor(Desenho desenho, int tamanho, Color cor) {
            this.desenho = desenho;
            this.tamanho = tamanho;
            this.cor = cor;
        }

        @Override public int getIconWidth() { return tamanho; }
        @Override public int getIconHeight() { return tamanho; }

        @Override
        public void paintIcon(Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.translate(x, y);
            g.setColor(cor);
            g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            desenho.pintar(g, tamanho, cor);
            g.dispose();
        }
    }

    // ------------------------------------------------------------- desenhos

    /** Sinal de "+" para acoes de adicionar. */
    public static final Desenho MAIS = (g, t, cor) -> {
        double m = t * 0.22, c = t / 2.0;
        g.draw(new java.awt.geom.Line2D.Double(c, m, c, t - m));
        g.draw(new java.awt.geom.Line2D.Double(m, c, t - m, c));
    };

    /** Lapis para editar. */
    public static final Desenho LAPIS = (g, t, cor) -> {
        double m = t * 0.18;
        Path2D p = new Path2D.Double();
        p.moveTo(m, t - m);
        p.lineTo(m, t - m - t * 0.22);
        p.lineTo(t - m - t * 0.22, m);
        p.lineTo(t - m, m + t * 0.22);
        p.lineTo(m + t * 0.22, t - m);
        p.closePath();
        g.draw(p);
        g.draw(new java.awt.geom.Line2D.Double(m, t - m, m + t * 0.22, t - m - t * 0.02));
    };

    /** Lixeira para excluir. */
    public static final Desenho LIXEIRA = (g, t, cor) -> {
        double m = t * 0.2;
        double topo = t * 0.3;
        g.draw(new java.awt.geom.Line2D.Double(m * 0.8, topo, t - m * 0.8, topo));       // tampa
        g.draw(new java.awt.geom.Line2D.Double(t * 0.4, topo - t * 0.08, t * 0.6, topo - t * 0.08)); // alca
        Path2D corpo = new Path2D.Double();
        corpo.moveTo(m, topo);
        corpo.lineTo(m + t * 0.06, t - m);
        corpo.lineTo(t - m - t * 0.06, t - m);
        corpo.lineTo(t - m, topo);
        g.draw(corpo);
        g.draw(new java.awt.geom.Line2D.Double(t * 0.42, topo + t * 0.12, t * 0.42, t - m - t * 0.08));
        g.draw(new java.awt.geom.Line2D.Double(t * 0.58, topo + t * 0.12, t * 0.58, t - m - t * 0.08));
    };

    /** Setas circulares para alternar status. */
    public static final Desenho RECICLAR = (g, t, cor) -> {
        double m = t * 0.2;
        java.awt.geom.Arc2D arco1 = new java.awt.geom.Arc2D.Double(m, m, t - 2 * m, t - 2 * m, 60, 200, java.awt.geom.Arc2D.OPEN);
        g.draw(arco1);
        double ang = Math.toRadians(60);
        double cx = t / 2.0, cy = t / 2.0, r = (t - 2 * m) / 2.0;
        double px = cx + r * Math.cos(ang), py = cy - r * Math.sin(ang);
        Path2D ponta = new Path2D.Double();
        ponta.moveTo(px - t * 0.12, py - t * 0.02);
        ponta.lineTo(px, py);
        ponta.lineTo(px + t * 0.02, py - t * 0.14);
        g.draw(ponta);
    };

    /** Lupa para busca. */
    public static final Desenho LUPA = (g, t, cor) -> {
        double d = t * 0.56;
        g.draw(new Ellipse2D.Double(t * 0.1, t * 0.1, d, d));
        g.draw(new java.awt.geom.Line2D.Double(t * 0.1 + d * 0.86, t * 0.1 + d * 0.86, t * 0.86, t * 0.86));
    };

    /** Funil para filtros. */
    public static final Desenho FUNIL = (g, t, cor) -> {
        double m = t * 0.18;
        Path2D p = new Path2D.Double();
        p.moveTo(m, m);
        p.lineTo(t - m, m);
        p.lineTo(t * 0.6, t * 0.5);
        p.lineTo(t * 0.6, t - m);
        p.lineTo(t * 0.4, t - m * 1.6);
        p.lineTo(t * 0.4, t * 0.5);
        p.closePath();
        g.draw(p);
    };

    /** Vassoura/limpar (X dentro de circulo suave). */
    public static final Desenho LIMPAR = (g, t, cor) -> {
        double m = t * 0.26;
        g.draw(new java.awt.geom.Line2D.Double(m, m, t - m, t - m));
        g.draw(new java.awt.geom.Line2D.Double(t - m, m, m, t - m));
    };

    /** Banco de dados (cilindro) para o cabecalho. */
    public static final Desenho BANCO = (g, t, cor) -> {
        double m = t * 0.16, w = t - 2 * m, h = t - 2 * m;
        double ry = h * 0.16;
        g.draw(new Ellipse2D.Double(m, m, w, ry * 2));
        g.draw(new java.awt.geom.Line2D.Double(m, m + ry, m, m + h - ry));
        g.draw(new java.awt.geom.Line2D.Double(m + w, m + ry, m + w, m + h - ry));
        java.awt.geom.Arc2D base = new java.awt.geom.Arc2D.Double(m, m + h - 2 * ry, w, ry * 2, 180, 180, java.awt.geom.Arc2D.OPEN);
        g.draw(base);
        java.awt.geom.Arc2D meio = new java.awt.geom.Arc2D.Double(m, m + h / 2 - ry, w, ry * 2, 180, 180, java.awt.geom.Arc2D.OPEN);
        g.draw(meio);
    };
}
