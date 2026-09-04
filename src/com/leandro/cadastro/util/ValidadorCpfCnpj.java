package com.leandro.cadastro.util;

/**
 * Valida CPF (11 digitos) e CNPJ (14 digitos) pelos digitos verificadores.
 * Aceita a entrada com ou sem mascara (pontos, tracos, barras).
 */
public final class ValidadorCpfCnpj {

    private ValidadorCpfCnpj() {
    }

    /** Remove tudo que nao for digito. */
    public static String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    public static boolean valido(String valor) {
        String d = somenteDigitos(valor);
        if (d.length() == 11) {
            return cpfValido(d);
        }
        if (d.length() == 14) {
            return cnpjValido(d);
        }
        return false;
    }

    public static boolean cpfValido(String cpf) {
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            return false;
        }
        int d1 = digitoVerificador(cpf, 9, 10);
        int d2 = digitoVerificador(cpf, 10, 11);
        return d1 == (cpf.charAt(9) - '0') && d2 == (cpf.charAt(10) - '0');
    }

    public static boolean cnpjValido(String cnpj) {
        if (cnpj.length() != 14 || cnpj.chars().distinct().count() == 1) {
            return false;
        }
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int d1 = digitoCnpj(cnpj, pesos1);
        int d2 = digitoCnpj(cnpj, pesos2);
        return d1 == (cnpj.charAt(12) - '0') && d2 == (cnpj.charAt(13) - '0');
    }

    private static int digitoVerificador(String cpf, int tamanho, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            soma += (cpf.charAt(i) - '0') * (pesoInicial - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static int digitoCnpj(String cnpj, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += (cnpj.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    /** Formata para 000.000.000-00 ou 00.000.000/0000-00; devolve original se invalido. */
    public static String formatar(String valor) {
        String d = somenteDigitos(valor);
        if (d.length() == 11) {
            return d.substring(0, 3) + "." + d.substring(3, 6) + "."
                    + d.substring(6, 9) + "-" + d.substring(9);
        }
        if (d.length() == 14) {
            return d.substring(0, 2) + "." + d.substring(2, 5) + "."
                    + d.substring(5, 8) + "/" + d.substring(8, 12) + "-" + d.substring(12);
        }
        return valor == null ? "" : valor;
    }
}
