package com.leandro.cadastro.repository;

import com.leandro.cadastro.model.Cliente;
import com.leandro.cadastro.model.Endereco;
import com.leandro.cadastro.model.StatusCliente;
import com.leandro.cadastro.util.Env;
import com.leandro.cadastro.util.Json;
import com.leandro.cadastro.util.ValidadorCpfCnpj;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementacao de {@link ClienteRepository} que fala com a API REST do
 * Supabase (PostgREST) via HTTP. Usa somente a biblioteca padrao
 * ({@link HttpClient}) e o mini-JSON em {@link Json}.
 *
 * <p>Configuracao lida do arquivo {@code .env}:
 * {@code SUPABASE_URL} (ou {@code SUPABASE_REST_URL}), {@code SUPABASE_ANON_KEY}
 * e, opcionalmente, {@code SUPABASE_TABLE_CLIENTES} (padrao {@code clientes}).</p>
 */
public class ClienteRepositorySupabase implements ClienteRepository {

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final String baseRest;   // ex.: https://xxx.supabase.co/rest/v1
    private final String apiKey;
    private final String tabela;
    private final String endpoint;   // baseRest + "/" + tabela

    public ClienteRepositorySupabase() {
        String rest = Env.get("SUPABASE_REST_URL");
        if (rest == null || rest.isBlank()) {
            rest = Env.obrigatorio("SUPABASE_URL").replaceAll("/+$", "") + "/rest/v1";
        }
        this.baseRest = rest.replaceAll("/+$", "");
        this.apiKey = Env.obrigatorio("SUPABASE_ANON_KEY");
        this.tabela = Env.get("SUPABASE_TABLE_CLIENTES", "clientes");
        this.endpoint = baseRest + "/" + tabela;
    }

    /** Testa a conexao; lanca RuntimeException com uma mensagem clara se falhar. */
    public void verificarConexao() {
        enviar(requisicao(endpoint + "?select=id&limit=1").GET().build());
    }

    // ------------------------------------------------------------- leitura

    @Override
    public List<Cliente> buscarTodos() {
        String corpo = enviar(requisicao(endpoint + "?select=*&order=nome.asc").GET().build());
        List<Cliente> lista = new ArrayList<>();
        for (Map<String, Object> linha : Json.parseLista(corpo)) {
            lista.add(daLinha(linha));
        }
        return lista;
    }

    @Override
    public Optional<Cliente> buscarPorId(long id) {
        String corpo = enviar(requisicao(endpoint + "?select=*&id=eq." + id).GET().build());
        List<Map<String, Object>> linhas = Json.parseLista(corpo);
        return linhas.isEmpty() ? Optional.empty() : Optional.of(daLinha(linhas.get(0)));
    }

    @Override
    public boolean existeCpfCnpj(String cpfCnpj, Long idIgnorar) {
        String doc = ValidadorCpfCnpj.somenteDigitos(cpfCnpj);
        if (doc.isEmpty()) {
            return false;
        }
        String url = endpoint + "?select=id&cpf_cnpj=eq." + enc(doc);
        if (idIgnorar != null) {
            url += "&id=neq." + idIgnorar;
        }
        String corpo = enviar(requisicao(url).GET().build());
        return !Json.parseLista(corpo).isEmpty();
    }

    // ------------------------------------------------------------- escrita

    @Override
    public Cliente salvar(Cliente cliente) {
        String json = Json.write(List.of(paraLinha(cliente)));
        HttpRequest req;
        if (cliente.getId() == null) {
            req = requisicao(endpoint)
                    .header("Prefer", "return=representation")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();
        } else {
            req = requisicao(endpoint + "?id=eq." + cliente.getId())
                    .header("Prefer", "return=representation")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();
        }
        String corpo = enviar(req);
        List<Map<String, Object>> linhas = Json.parseLista(corpo);
        if (!linhas.isEmpty()) {
            Cliente salvo = daLinha(linhas.get(0));
            cliente.setId(salvo.getId());
            cliente.setDataCadastro(salvo.getDataCadastro());
        }
        return cliente;
    }

    @Override
    public boolean remover(long id) {
        HttpRequest req = requisicao(endpoint + "?id=eq." + id)
                .header("Prefer", "return=representation")
                .DELETE()
                .build();
        String corpo = enviar(req);
        return !Json.parseLista(corpo).isEmpty();
    }

    // ------------------------------------------------------- mapeamento

    /** Cliente -> objeto JSON com os nomes de coluna da tabela. */
    private Map<String, Object> paraLinha(Cliente c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("nome", c.getNome());
        m.put("cpf_cnpj", ValidadorCpfCnpj.somenteDigitos(c.getCpfCnpj()));
        m.put("email", vazioParaNull(c.getEmail()));
        m.put("telefone", vazioParaNull(c.getTelefone()));
        m.put("cidade", vazioParaNull(c.getEndereco().getCidade()));
        m.put("uf", vazioParaNull(c.getEndereco().getUf()));
        m.put("status", c.getStatus().name());
        m.put("observacoes", vazioParaNull(c.getObservacoes()));
        m.put("valor_a_receber", c.getValorAReceber());
        m.put("data_vencimento", c.getDataVencimento() == null ? null : c.getDataVencimento().toString());
        if (c.getDataCadastro() != null) {
            m.put("data_cadastro", c.getDataCadastro().toString());
        }
        return m;
    }

    /** Linha JSON da tabela -> Cliente. */
    private Cliente daLinha(Map<String, Object> linha) {
        Cliente c = new Cliente();
        c.setId(Json.inteiro(linha, "id"));
        c.setNome(Json.texto(linha, "nome"));
        c.setCpfCnpj(Json.texto(linha, "cpf_cnpj"));
        c.setEmail(Json.texto(linha, "email"));
        c.setTelefone(Json.texto(linha, "telefone"));

        Endereco end = new Endereco();
        end.setCidade(Json.texto(linha, "cidade"));
        end.setUf(Json.texto(linha, "uf"));
        c.setEndereco(end);

        String status = Json.texto(linha, "status");
        c.setStatus("INATIVO".equals(status) ? StatusCliente.INATIVO : StatusCliente.ATIVO);
        c.setObservacoes(Json.texto(linha, "observacoes"));

        BigDecimal valor = Json.numero(linha, "valor_a_receber");
        c.setValorAReceber(valor == null ? BigDecimal.ZERO : valor);

        String venc = Json.texto(linha, "data_vencimento");
        c.setDataVencimento(venc == null || venc.isBlank() ? null : LocalDate.parse(venc));

        String cad = Json.texto(linha, "data_cadastro");
        if (cad != null && !cad.isBlank()) {
            c.setDataCadastro(LocalDate.parse(cad));
        }
        return c;
    }

    // ---------------------------------------------------------- HTTP

    private HttpRequest.Builder requisicao(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("apikey", apiKey)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    private String enviar(HttpRequest req) {
        try {
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            int sc = resp.statusCode();
            if (sc >= 200 && sc < 300) {
                return resp.body();
            }
            throw new RuntimeException("Supabase respondeu HTTP " + sc + " em "
                    + req.method() + " " + req.uri().getPath()
                    + (resp.body() == null || resp.body().isBlank() ? "" : "\n" + resp.body()));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Falha de conexao com o Supabase: " + e.getMessage(), e);
        }
    }

    private static String enc(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }

    private static String vazioParaNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
