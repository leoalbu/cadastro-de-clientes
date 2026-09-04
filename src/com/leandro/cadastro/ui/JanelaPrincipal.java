package com.leandro.cadastro.ui;

import com.leandro.cadastro.model.Cliente;
import com.leandro.cadastro.model.SituacaoFinanceira;
import com.leandro.cadastro.model.StatusCliente;
import com.leandro.cadastro.service.ClienteService;
import com.leandro.cadastro.service.FiltroClientes;
import com.leandro.cadastro.service.ResumoFinanceiro;
import com.leandro.cadastro.ui.comp.BotaoModerno;
import com.leandro.cadastro.ui.comp.CampoBusca;
import com.leandro.cadastro.ui.comp.PainelCard;
import com.leandro.cadastro.ui.tema.Cores;
import com.leandro.cadastro.ui.tema.Fontes;
import com.leandro.cadastro.ui.tema.Icones;
import com.leandro.cadastro.util.Formatos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/** Janela principal: cabecalho, barra de filtros, tabela e rodape com resumo. */
public class JanelaPrincipal extends JFrame {

    private final ClienteService service;
    private final String fonteDados;

    private final ClienteTableModel tableModel = new ClienteTableModel();
    private final JTable tabela = new JTable(tableModel);

    private final CampoBusca campoBusca = new CampoBusca("Buscar por nome ou CPF/CNPJ", 22);
    private final JComboBox<Object> comboStatus = new JComboBox<>();
    private final JComboBox<Object> comboSituacao = new JComboBox<>();
    private final JSpinner spinnerDias = new JSpinner(new SpinnerNumberModel(7, 0, 365, 1));
    private final JCheckBox usarDias = new JCheckBox("Vence em ate");

    private final JLabel mQtd = valorMetrica();
    private final JLabel mTotal = valorMetrica();
    private final JLabel mVencidos = valorMetrica();

    private BotaoModerno btnEditar;
    private BotaoModerno btnExcluir;
    private BotaoModerno btnStatus;

    public JanelaPrincipal(ClienteService service, String fonteDados) {
        super("Cadastro de Clientes");
        this.service = service;
        this.fonteDados = fonteDados;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(880, 560));

        JPanel raiz = new JPanel(new BorderLayout(0, 14));
        raiz.setBackground(Cores.FUNDO);
        raiz.setBorder(new EmptyBorder(18, 20, 18, 20));
        setContentPane(raiz);

        raiz.add(construirCabecalho(), BorderLayout.NORTH);
        raiz.add(construirCentro(), BorderLayout.CENTER);
        raiz.add(construirRodape(), BorderLayout.SOUTH);

        atualizarTabela();
        atualizarBotoesSelecao();

        setSize(1180, 720);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    // =========================================================== cabecalho

    private JComponent construirCabecalho() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setOpaque(false);

        JLabel icone = new JLabel(Icones.de(Icones.BANCO, 26, Cores.ACENTO));
        icone.setBorder(new EmptyBorder(0, 0, 0, 12));

        JLabel titulo = new JLabel("Cadastro de Clientes");
        titulo.setFont(Fontes.semibold(22f));
        titulo.setForeground(Cores.TINTA);

        JLabel sub = new JLabel("Contas a receber, vencimentos e filtros");
        sub.setFont(Fontes.regular(12.5f));
        sub.setForeground(Cores.TINTA_SUAVE);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(sub);

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        esquerda.setOpaque(false);
        esquerda.add(icone);
        esquerda.add(textos);

        BotaoModerno novo = new BotaoModerno("Novo cliente",
                Icones.de(Icones.MAIS, 16, Color.WHITE), BotaoModerno.Tipo.PRIMARIO);
        novo.addActionListener(e -> novoCliente());

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        direita.setOpaque(false);
        direita.add(chipFonteDados());
        direita.add(novo);

        painel.add(esquerda, BorderLayout.WEST);
        painel.add(direita, BorderLayout.EAST);
        return painel;
    }

    private JComponent chipFonteDados() {
        boolean supabase = fonteDados != null && fonteDados.toLowerCase().contains("supa");
        Color cor = supabase ? Cores.ACENTO : Cores.TINTA_FRACA;
        Color fundo = supabase ? Cores.ACENTO_SUAVE : Cores.INATIVO_BG;
        JLabel chip = new JLabel("●  " + (fonteDados == null ? "Memoria" : fonteDados));
        chip.setFont(Fontes.semibold(11.5f));
        chip.setForeground(cor);
        chip.setOpaque(true);
        chip.setBackground(fundo);
        chip.setBorder(new EmptyBorder(6, 12, 6, 12));
        return chip;
    }

    // ============================================================== centro

    private JComponent construirCentro() {
        JPanel centro = new JPanel(new BorderLayout(0, 14));
        centro.setOpaque(false);
        centro.add(construirFiltros(), BorderLayout.NORTH);
        centro.add(construirTabela(), BorderLayout.CENTER);
        return centro;
    }

    private JComponent construirFiltros() {
        comboStatus.addItem("Todos os status");
        for (StatusCliente s : StatusCliente.values()) comboStatus.addItem(s);
        comboSituacao.addItem("Todas as situacoes");
        for (SituacaoFinanceira s : SituacaoFinanceira.values()) comboSituacao.addItem(s);

        estilizarCombo(comboStatus);
        estilizarCombo(comboSituacao);

        usarDias.setOpaque(false);
        usarDias.setFont(Fontes.regular(12.5f));
        usarDias.setForeground(Cores.TINTA_SUAVE);
        usarDias.setFocusPainted(false);

        spinnerDias.setFont(Fontes.regular(12.5f));
        ((JSpinner.DefaultEditor) spinnerDias.getEditor()).getTextField().setColumns(3);
        spinnerDias.setEnabled(false);

        JLabel dias = new JLabel("dias");
        dias.setFont(Fontes.regular(12.5f));
        dias.setForeground(Cores.TINTA_SUAVE);

        BotaoModerno filtrar = new BotaoModerno("Filtrar",
                Icones.de(Icones.FUNIL, 15, Cores.TINTA), BotaoModerno.Tipo.SECUNDARIO);
        filtrar.addActionListener(e -> atualizarTabela());
        BotaoModerno limpar = new BotaoModerno("Limpar",
                Icones.de(Icones.LIMPAR, 14, Cores.TINTA_SUAVE), BotaoModerno.Tipo.GHOST);
        limpar.addActionListener(e -> limparFiltros());

        campoBusca.addActionListener(e -> atualizarTabela());
        campoBusca.getDocument().addDocumentListener(new SimplesDocListener(this::atualizarTabela));
        comboStatus.addActionListener(e -> atualizarTabela());
        comboSituacao.addActionListener(e -> atualizarTabela());
        usarDias.addActionListener(e -> {
            spinnerDias.setEnabled(usarDias.isSelected());
            atualizarTabela();
        });
        spinnerDias.addChangeListener(e -> {
            if (usarDias.isSelected()) atualizarTabela();
        });

        PainelCard card = new PainelCard(new FlowLayout(FlowLayout.LEFT, 10, 8), 12, 12, 14, 12, 14);
        card.add(rotulado("Busca", campoBusca));
        card.add(rotulado("Status", comboStatus));
        card.add(rotulado("Situacao", comboSituacao));
        JPanel diasBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        diasBox.setOpaque(false);
        diasBox.add(usarDias);
        diasBox.add(spinnerDias);
        diasBox.add(dias);
        card.add(rotulado("Vencimento proximo", diasBox));
        card.add(Box.createHorizontalStrut(6));
        card.add(filtrar);
        card.add(limpar);
        return card;
    }

    private JComponent construirTabela() {
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoCreateRowSorter(true);
        tabela.setFont(Fontes.regular(13f));
        tabela.setBackground(Cores.SUPERFICIE);
        tabela.setForeground(Cores.TINTA);
        tabela.getSelectionModel().addListSelectionListener(e -> atualizarBotoesSelecao());

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setDefaultRenderer(new RenderizadorCabecalho(cabecalho.getDefaultRenderer()));
        cabecalho.setPreferredSize(new Dimension(0, 40));

        RenderizadorTabela.aplicar(tabela, tableModel);

        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tabela.getSelectedRow() >= 0) {
                    editarSelecionado();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Cores.SUPERFICIE);

        PainelCard card = new PainelCard(new BorderLayout(), 12, 1, 1, 1, 1);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ============================================================== rodape

    private JComponent construirRodape() {
        btnEditar = new BotaoModerno("Editar",
                Icones.de(Icones.LAPIS, 15, Cores.TINTA), BotaoModerno.Tipo.SECUNDARIO);
        btnEditar.addActionListener(e -> editarSelecionado());
        btnExcluir = new BotaoModerno("Excluir",
                Icones.de(Icones.LIXEIRA, 15, Cores.VENCIDO), BotaoModerno.Tipo.SECUNDARIO);
        btnExcluir.setForeground(Cores.VENCIDO);
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnStatus = new BotaoModerno("Ativar / Inativar",
                Icones.de(Icones.RECICLAR, 15, Cores.TINTA), BotaoModerno.Tipo.GHOST);
        btnStatus.addActionListener(e -> alternarStatus());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botoes.setOpaque(false);
        botoes.add(btnEditar);
        botoes.add(btnExcluir);
        botoes.add(btnStatus);

        JPanel metricas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 26, 0));
        metricas.setOpaque(false);
        metricas.add(metrica("CLIENTES", mQtd));
        metricas.add(metrica("TOTAL A RECEBER", mTotal));
        metricas.add(metrica("VENCIDOS", mVencidos));

        PainelCard card = new PainelCard(new BorderLayout(), 12, 10, 14, 10, 16);
        card.add(botoes, BorderLayout.WEST);
        card.add(metricas, BorderLayout.EAST);
        return card;
    }

    private JComponent metrica(String rotulo, JLabel valor) {
        JLabel k = new JLabel(rotulo);
        k.setFont(Fontes.semibold(10f));
        k.setForeground(Cores.TINTA_FRACA);
        k.setAlignmentX(RIGHT_ALIGNMENT);
        valor.setAlignmentX(RIGHT_ALIGNMENT);

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.add(k);
        box.add(Box.createVerticalStrut(2));
        box.add(valor);
        return box;
    }

    private static JLabel valorMetrica() {
        JLabel l = new JLabel("-");
        l.setFont(Fontes.semibold(16f));
        l.setForeground(Cores.TINTA);
        return l;
    }

    // ============================================================ helpers UI

    private JComponent rotulado(String texto, JComponent campo) {
        JLabel l = new JLabel(texto.toUpperCase());
        l.setFont(Fontes.semibold(10f));
        l.setForeground(Cores.TINTA_FRACA);
        l.setAlignmentX(LEFT_ALIGNMENT);
        campo.setAlignmentX(LEFT_ALIGNMENT);

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.add(l);
        box.add(Box.createVerticalStrut(4));
        box.add(campo);
        return box;
    }

    private void estilizarCombo(JComboBox<?> combo) {
        combo.setFont(Fontes.regular(12.5f));
        combo.setBackground(Cores.SUPERFICIE);
        combo.setForeground(Cores.TINTA);
        combo.setFocusable(false);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Cores.BORDA_FORTE, 1, true),
                new EmptyBorder(5, 8, 5, 6)));
    }

    private void atualizarBotoesSelecao() {
        boolean tem = tabela.getSelectedRow() >= 0;
        if (btnEditar != null) btnEditar.setEnabled(tem);
        if (btnExcluir != null) btnExcluir.setEnabled(tem);
        if (btnStatus != null) btnStatus.setEnabled(tem);
    }

    // =========================================================== acoes

    private FiltroClientes filtroAtual() {
        FiltroClientes filtro = new FiltroClientes();
        filtro.setTexto(campoBusca.getText());
        if (comboStatus.getSelectedItem() instanceof StatusCliente s) filtro.setStatus(s);
        if (comboSituacao.getSelectedItem() instanceof SituacaoFinanceira s) filtro.setSituacao(s);
        if (usarDias.isSelected()) filtro.setVenceEmDias((Integer) spinnerDias.getValue());
        return filtro;
    }

    private void atualizarTabela() {
        try {
            List<Cliente> lista = service.filtrar(filtroAtual());
            tableModel.setClientes(lista);
            ResumoFinanceiro resumo = service.resumo(lista);
            mQtd.setText(String.valueOf(resumo.getQuantidadeClientes()));
            mTotal.setText(Formatos.moeda(resumo.getTotalAReceber()));
            mVencidos.setText(resumo.getQuantidadeVencidos()
                    + "  (" + Formatos.moeda(resumo.getTotalVencido()) + ")");
            mVencidos.setForeground(resumo.getQuantidadeVencidos() > 0 ? Cores.VENCIDO : Cores.TINTA);
        } catch (RuntimeException e) {
            mostrarErro("Nao foi possivel carregar os clientes.\n\n" + e.getMessage());
        }
    }

    private void limparFiltros() {
        campoBusca.setText("");
        comboStatus.setSelectedIndex(0);
        comboSituacao.setSelectedIndex(0);
        usarDias.setSelected(false);
        spinnerDias.setValue(7);
        spinnerDias.setEnabled(false);
        atualizarTabela();
    }

    private Cliente selecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return null;
        return tableModel.getClienteEm(tabela.convertRowIndexToModel(linha));
    }

    private void novoCliente() {
        if (FormularioClienteDialog.abrir(this, service, new Cliente())) {
            atualizarTabela();
        }
    }

    private void editarSelecionado() {
        Cliente c = selecionado();
        if (c == null) { avisarSelecione(); return; }
        if (FormularioClienteDialog.abrir(this, service, c)) {
            atualizarTabela();
        }
    }

    private void excluirSelecionado() {
        Cliente c = selecionado();
        if (c == null) { avisarSelecione(); return; }
        int opcao = JOptionPane.showConfirmDialog(this,
                "Excluir definitivamente o cliente \"" + c.getNome() + "\"?",
                "Confirmar exclusao", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opcao == JOptionPane.YES_OPTION) {
            try {
                service.excluir(c.getId());
                atualizarTabela();
            } catch (RuntimeException ex) {
                mostrarErro("Nao foi possivel excluir.\n\n" + ex.getMessage());
            }
        }
    }

    private void alternarStatus() {
        Cliente c = selecionado();
        if (c == null) { avisarSelecione(); return; }
        try {
            if (c.getStatus() == StatusCliente.ATIVO) service.inativar(c.getId());
            else service.reativar(c.getId());
            atualizarTabela();
        } catch (RuntimeException ex) {
            mostrarErro("Nao foi possivel alterar o status.\n\n" + ex.getMessage());
        }
    }

    private void avisarSelecione() {
        JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.",
                "Nenhum cliente selecionado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    // -------- listener de documento compacto --------
    private static final class SimplesDocListener implements javax.swing.event.DocumentListener {
        private final Runnable acao;
        SimplesDocListener(Runnable acao) { this.acao = acao; }
        public void insertUpdate(javax.swing.event.DocumentEvent e) { acao.run(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { acao.run(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { acao.run(); }
    }
}
