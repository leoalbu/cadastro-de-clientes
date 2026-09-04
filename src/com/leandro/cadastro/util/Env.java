package com.leandro.cadastro.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Leitor minimalista de arquivo {@code .env} (uma linha {@code CHAVE=valor}).
 * Sem dependencias externas. Procura o arquivo na pasta de onde o programa
 * foi executado (raiz do projeto).
 *
 * <p>Ordem de resolucao em {@link #get(String)}: primeiro o {@code .env},
 * depois a variavel de ambiente do sistema operacional.</p>
 */
public final class Env {

    private static final Map<String, String> VALORES = carregar();

    private Env() {
    }

    private static Map<String, String> carregar() {
        Map<String, String> mapa = new HashMap<>();
        Path arquivo = Path.of(".env");
        if (!Files.exists(arquivo)) {
            return mapa;
        }
        try {
            for (String linha : Files.readAllLines(arquivo)) {
                String l = linha.strip();
                if (l.isEmpty() || l.startsWith("#") || !l.contains("=")) {
                    continue;
                }
                int i = l.indexOf('=');
                String chave = l.substring(0, i).strip();
                String valor = l.substring(i + 1).strip();
                if (valor.length() >= 2
                        && valor.startsWith("\"") && valor.endsWith("\"")) {
                    valor = valor.substring(1, valor.length() - 1);
                }
                mapa.put(chave, valor);
            }
        } catch (IOException e) {
            System.err.println("Nao foi possivel ler o arquivo .env: " + e.getMessage());
        }
        return mapa;
    }

    public static String get(String chave) {
        String v = VALORES.get(chave);
        return v != null ? v : System.getenv(chave);
    }

    public static String get(String chave, String padrao) {
        String v = get(chave);
        return (v == null || v.isBlank()) ? padrao : v;
    }

    /** Igual a {@link #get(String)}, mas lanca erro claro se estiver ausente. */
    public static String obrigatorio(String chave) {
        String v = get(chave);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException(
                    "Configuracao ausente: " + chave + ". Defina no arquivo .env "
                    + "(veja .env.example).");
        }
        return v;
    }
}
