package com.leandro.cadastro.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formatacao e parsing de moeda e datas no padrao brasileiro. */
public final class Formatos {

    private static final Locale BR = Locale.of("pt", "BR");
    public static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Formatos() {
    }

    public static String moeda(BigDecimal valor) {
        BigDecimal v = valor == null ? BigDecimal.ZERO : valor;
        return NumberFormat.getCurrencyInstance(BR).format(v);
    }

    public static String data(LocalDate data) {
        return data == null ? "" : data.format(DATA);
    }

    /**
     * Converte "dd/MM/yyyy" para LocalDate. Devolve {@code null} se o texto for
     * vazio; lanca {@link java.time.format.DateTimeParseException} se invalido.
     */
    public static LocalDate parseData(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return LocalDate.parse(texto.trim(), DATA);
    }

    /**
     * Converte texto de valor ("1.234,56" ou "1234.56" ou "1234,56") para
     * BigDecimal. Devolve ZERO se vazio.
     */
    public static BigDecimal parseValor(String texto) {
        if (texto == null || texto.isBlank()) {
            return BigDecimal.ZERO;
        }
        String limpo = texto.trim()
                .replace("R$", "")
                .replace(" ", "")
                .replace(".", "")
                .replace(",", ".");
        return new BigDecimal(limpo);
    }
}
