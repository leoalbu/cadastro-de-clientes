package com.leandro.cadastro.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lancada pelo service quando os dados de um cliente nao passam nas regras de
 * validacao. Pode carregar varias mensagens de uma vez.
 */
public class ValidacaoException extends RuntimeException {

    private final List<String> erros;

    public ValidacaoException(List<String> erros) {
        super(String.join("\n", erros));
        this.erros = new ArrayList<>(erros);
    }

    public ValidacaoException(String erro) {
        this(List.of(erro));
    }

    public List<String> getErros() {
        return Collections.unmodifiableList(erros);
    }
}
