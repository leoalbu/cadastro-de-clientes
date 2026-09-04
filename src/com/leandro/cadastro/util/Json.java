package com.leandro.cadastro.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mini leitor/escritor de JSON, sem dependencias externas. Suficiente para
 * conversar com a API REST do Supabase (PostgREST), que troca objetos planos
 * e arrays desses objetos.
 *
 * <p>Tipos produzidos por {@link #parse(String)}:
 * {@code Map<String,Object>}, {@code List<Object>}, {@code String},
 * {@link BigDecimal}, {@link Boolean} e {@code null}.</p>
 */
public final class Json {

    private Json() {
    }

    // ------------------------------------------------------------- PARSE

    public static Object parse(String texto) {
        return new Parser(texto).parseCompleto();
    }

    /** Conveniencia: interpreta o texto como um array de objetos. */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> parseLista(String texto) {
        Object raiz = parse(texto);
        List<Map<String, Object>> saida = new ArrayList<>();
        if (raiz instanceof List<?> lista) {
            for (Object item : lista) {
                if (item instanceof Map<?, ?> mapa) {
                    saida.add((Map<String, Object>) mapa);
                }
            }
        } else if (raiz instanceof Map<?, ?> mapa) {
            saida.add((Map<String, Object>) mapa);
        }
        return saida;
    }

    private static final class Parser {
        private final String s;
        private int i;

        Parser(String s) {
            this.s = s;
        }

        Object parseCompleto() {
            pularEspacos();
            Object v = valor();
            pularEspacos();
            return v;
        }

        private Object valor() {
            pularEspacos();
            char c = s.charAt(i);
            switch (c) {
                case '{': return objeto();
                case '[': return array();
                case '"': return string();
                case 't': i += 4; return Boolean.TRUE;
                case 'f': i += 5; return Boolean.FALSE;
                case 'n': i += 4; return null;
                default:  return numero();
            }
        }

        private Map<String, Object> objeto() {
            Map<String, Object> mapa = new LinkedHashMap<>();
            i++; // {
            pularEspacos();
            if (s.charAt(i) == '}') { i++; return mapa; }
            while (true) {
                pularEspacos();
                String chave = string();
                pularEspacos();
                i++; // :
                mapa.put(chave, valor());
                pularEspacos();
                char c = s.charAt(i++);
                if (c == '}') break;
                // c == ','
            }
            return mapa;
        }

        private List<Object> array() {
            List<Object> lista = new ArrayList<>();
            i++; // [
            pularEspacos();
            if (s.charAt(i) == ']') { i++; return lista; }
            while (true) {
                lista.add(valor());
                pularEspacos();
                char c = s.charAt(i++);
                if (c == ']') break;
                // c == ','
            }
            return lista;
        }

        private String string() {
            StringBuilder sb = new StringBuilder();
            i++; // abre aspas
            while (true) {
                char c = s.charAt(i++);
                if (c == '"') break;
                if (c == '\\') {
                    char e = s.charAt(i++);
                    switch (e) {
                        case '"':  sb.append('"');  break;
                        case '\\': sb.append('\\'); break;
                        case '/':  sb.append('/');  break;
                        case 'b':  sb.append('\b'); break;
                        case 'f':  sb.append('\f'); break;
                        case 'n':  sb.append('\n'); break;
                        case 'r':  sb.append('\r'); break;
                        case 't':  sb.append('\t'); break;
                        case 'u':
                            sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                            i += 4;
                            break;
                        default:   sb.append(e);
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        private BigDecimal numero() {
            int ini = i;
            while (i < s.length() && "+-0123456789.eE".indexOf(s.charAt(i)) >= 0) {
                i++;
            }
            return new BigDecimal(s.substring(ini, i));
        }

        private void pularEspacos() {
            while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
                i++;
            }
        }
    }

    // ------------------------------------------------------------- WRITE

    /** Serializa Map/List/String/Number/Boolean/null para JSON. */
    public static String write(Object valor) {
        StringBuilder sb = new StringBuilder();
        escrever(valor, sb);
        return sb.toString();
    }

    private static void escrever(Object v, StringBuilder sb) {
        if (v == null) {
            sb.append("null");
        } else if (v instanceof String str) {
            escreverString(str, sb);
        } else if (v instanceof Boolean || v instanceof Number) {
            sb.append(v.toString());
        } else if (v instanceof Map<?, ?> mapa) {
            sb.append('{');
            boolean primeiro = true;
            for (Map.Entry<?, ?> e : mapa.entrySet()) {
                if (!primeiro) sb.append(',');
                primeiro = false;
                escreverString(String.valueOf(e.getKey()), sb);
                sb.append(':');
                escrever(e.getValue(), sb);
            }
            sb.append('}');
        } else if (v instanceof Iterable<?> it) {
            sb.append('[');
            boolean primeiro = true;
            for (Object item : it) {
                if (!primeiro) sb.append(',');
                primeiro = false;
                escrever(item, sb);
            }
            sb.append(']');
        } else {
            escreverString(v.toString(), sb);
        }
    }

    private static void escreverString(String str, StringBuilder sb) {
        sb.append('"');
        for (int k = 0; k < str.length(); k++) {
            char c = str.charAt(k);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                case '\b': sb.append("\\b");  break;
                case '\f': sb.append("\\f");  break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
    }

    // ---------------------------------------------------- acesso tipado

    public static String texto(Map<String, Object> obj, String chave) {
        Object v = obj.get(chave);
        return v == null ? null : v.toString();
    }

    public static BigDecimal numero(Map<String, Object> obj, String chave) {
        Object v = obj.get(chave);
        if (v == null) return null;
        return (v instanceof BigDecimal bd) ? bd : new BigDecimal(v.toString());
    }

    public static Long inteiro(Map<String, Object> obj, String chave) {
        BigDecimal n = numero(obj, chave);
        return n == null ? null : n.longValue();
    }
}
