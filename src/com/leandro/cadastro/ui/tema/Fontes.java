package com.leandro.cadastro.ui.tema;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;

/** Escolha de fonte com fallback previsivel entre sistemas. */
public final class Fontes {

    private Fontes() {
    }

    private static final String FAMILIA = escolherFamilia();

    private static String escolherFamilia() {
        String[] preferidas = {"Segoe UI", "Inter", "SF Pro Text", "Roboto", "Noto Sans"};
        var disponiveis = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String f : preferidas) {
            if (disponiveis.contains(f)) {
                return f;
            }
        }
        return Font.SANS_SERIF;
    }

    public static Font regular(float tamanho) {
        return new Font(FAMILIA, Font.PLAIN, Math.round(tamanho));
    }

    public static Font semibold(float tamanho) {
        return new Font(FAMILIA, Font.BOLD, Math.round(tamanho));
    }
}
