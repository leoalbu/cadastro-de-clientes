package com.leandro.cadastro.util;

import java.util.regex.Pattern;

/** Validacao simples de formato de e-mail. */
public final class ValidadorEmail {

    private static final Pattern PADRAO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private ValidadorEmail() {
    }

    public static boolean valido(String email) {
        return email != null && PADRAO.matcher(email.trim()).matches();
    }
}
