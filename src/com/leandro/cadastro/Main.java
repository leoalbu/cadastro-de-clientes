package com.leandro.cadastro;

import com.leandro.cadastro.model.Cliente;
import com.leandro.cadastro.model.Endereco;
import com.leandro.cadastro.model.StatusCliente;
import com.leandro.cadastro.repository.ClienteRepository;
import com.leandro.cadastro.repository.ClienteRepositoryMemoria;
import com.leandro.cadastro.repository.ClienteRepositorySupabase;
import com.leandro.cadastro.service.ClienteService;
import com.leandro.cadastro.ui.JanelaPrincipal;
import com.leandro.cadastro.util.Env;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Ponto de entrada. Monta as camadas (repositorio -> service -> UI) e abre a
 * janela. Se o arquivo {@code .env} estiver configurado com um projeto
 * Supabase, usa {@link ClienteRepositorySupabase}; caso contrario, cai para
 * {@link ClienteRepositoryMemoria} com dados de exemplo.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignorado) {
            // segue com o look and feel padrao
        }

        ClienteRepository repository = criarRepositorio();
        ClienteService service = new ClienteService(repository);
        boolean memoria = repository instanceof ClienteRepositoryMemoria;
        if (memoria) {
            carregarExemplos(service);
        }
        String fonteDados = memoria ? "Memoria" : "Supabase";

        SwingUtilities.invokeLater(() ->
                new JanelaPrincipal(service, fonteDados).setVisible(true));
    }

    /** Decide qual repositorio usar com base no arquivo {@code .env}. */
    private static ClienteRepository criarRepositorio() {
        String url = Env.get("SUPABASE_URL");
        boolean configurado = url != null && !url.isBlank() && !url.contains("SEU-PROJETO");

        if (!configurado) {
            System.out.println("[info] Supabase nao configurado no .env - usando dados em memoria.");
            return new ClienteRepositoryMemoria();
        }

        try {
            ClienteRepositorySupabase supabase = new ClienteRepositorySupabase();
            supabase.verificarConexao();
            System.out.println("[info] Conectado ao Supabase: " + url);
            return supabase;
        } catch (RuntimeException e) {
            System.err.println("[erro] Nao foi possivel conectar ao Supabase:\n" + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Nao foi possivel conectar ao Supabase:\n\n" + e.getMessage()
                    + "\n\nO app vai abrir com dados em memoria.",
                    "Supabase indisponivel", JOptionPane.WARNING_MESSAGE);
            return new ClienteRepositoryMemoria();
        }
    }

    private static void carregarExemplos(ClienteService service) {
        LocalDate hoje = LocalDate.now();

        service.salvar(criar("Maria Oliveira Souza", "52998224725",
                "maria.souza@email.com", "(11) 98888-1111",
                "Sao Paulo", "SP", new BigDecimal("1250.00"), hoje.minusDays(10),
                StatusCliente.ATIVO));

        service.salvar(criar("Joao Pedro Almeida", "39053344705",
                "joao.almeida@email.com", "(21) 97777-2222",
                "Rio de Janeiro", "RJ", new BigDecimal("480.50"), hoje.plusDays(3),
                StatusCliente.ATIVO));

        service.salvar(criar("Comercio Silva LTDA", "11444777000161",
                "contato@comerciosilva.com.br", "(31) 3333-4444",
                "Belo Horizonte", "MG", new BigDecimal("7200.00"), hoje.plusDays(25),
                StatusCliente.ATIVO));

        service.salvar(criar("Ana Carolina Lima", "45317828791",
                "ana.lima@email.com", "(41) 96666-3333",
                "Curitiba", "PR", BigDecimal.ZERO, null,
                StatusCliente.ATIVO));

        service.salvar(criar("Carlos Eduardo Ramos", "01234567890",
                "carlos.ramos@email.com", "(51) 95555-4444",
                "Porto Alegre", "RS", new BigDecimal("330.00"), hoje,
                StatusCliente.ATIVO));

        service.salvar(criar("Fernanda Costa", "15350946056",
                "fernanda.costa@email.com", "(85) 94444-5555",
                "Fortaleza", "CE", new BigDecimal("2100.00"), hoje.minusDays(45),
                StatusCliente.INATIVO));

        service.salvar(criar("Distribuidora Norte SA", "34028316000103",
                "financeiro@distnorte.com.br", "(92) 3232-1010",
                "Manaus", "AM", new BigDecimal("15400.00"), hoje.plusDays(6),
                StatusCliente.ATIVO));

        service.salvar(criar("Rafael Mendes", "11144477735",
                "rafael.mendes@email.com", "(62) 93333-6666",
                "Goiania", "GO", BigDecimal.ZERO, null,
                StatusCliente.INATIVO));
    }

    private static Cliente criar(String nome, String cpfCnpj, String email, String telefone,
                                 String cidade, String uf, BigDecimal valor,
                                 LocalDate vencimento, StatusCliente status) {
        Cliente c = new Cliente();
        c.setNome(nome);
        c.setCpfCnpj(cpfCnpj);
        c.setEmail(email);
        c.setTelefone(telefone);
        Endereco end = new Endereco();
        end.setCidade(cidade);
        end.setUf(uf);
        c.setEndereco(end);
        c.setValorAReceber(valor);
        c.setDataVencimento(vencimento);
        c.setStatus(status);
        return c;
    }
}
