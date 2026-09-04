package com.leandro.cadastro.ui;

import com.leandro.cadastro.exception.ValidacaoException;
import com.leandro.cadastro.model.Cliente;
import com.leandro.cadastro.model.Endereco;
import com.leandro.cadastro.model.StatusCliente;
import com.leandro.cadastro.service.ClienteService;
import com.leandro.cadastro.ui.comp.BotaoModerno;
import com.leandro.cadastro.ui.tema.Cores;
import com.leandro.cadastro.ui.tema.Fontes;
import com.leandro.cadastro.ui.tema.Icones;
import com.leandro.cadastro.util.Formatos;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.time.format.DateTimeParseException;

/**
 * Janela modal para criar ou editar um cliente. Chame
 * {@link #abrir(Window, ClienteService, Cliente)} e verifique o retorno:
 * {@code true} se o cliente foi salvo.
 */
public class FormularioClienteDialog extends JDialog {

    private final ClienteService service;
    private final Cliente cliente;
    private boolean salvo = false;

    private final JTextField nome = new JTextField(25);
    private final JTextField cpfCnpj = new JTextField(18);
    private final JTextField email = new JTextField(25);
    private final JTextField telefone = new JTextField(15);
    private final JTextField logradouro = new JTextField(20);
    private final JTextField numero = new JTextField(6);
    private final JTextField bairro = new JTextField(15);
    private final JTextField cidade = new JTextField(15);
    private final JTextField uf = new JTextField(4);
    private final JTextField cep = new JTextField(10);
    private final JComboBox<StatusCliente> status = new JComboBox<>(StatusCliente.values());
    private final JTextField valorAReceber = new JTextField(12);
    private final JTextField dataVencimento = new JTextField(12);
    private final JTextArea observacoes = new JTextArea(3, 25);

    private FormularioClienteDialog(Window dono, ClienteService service, Cliente cliente) {
        super(dono, cliente.getId() == null ? "Novo cliente" : "Editar cliente",
                ModalityType.APPLICATION_MODAL);
        this.service = service;
        this.cliente = cliente;
        montarLayout();
        preencherCampos();
        pack();
        setLocationRelativeTo(dono);
    }

    public static boolean abrir(Window dono, ClienteService service, Cliente cliente) {
        FormularioClienteDialog dialog = new FormularioClienteDialog(dono, service, cliente);
        dialog.setVisible(true);
        return dialog.salvo;
    }

    private void montarLayout() {
        for (JTextField tf : new JTextField[]{nome, cpfCnpj, email, telefone, logradouro,
                numero, bairro, cidade, uf, cep, valorAReceber, dataVencimento}) {
            estilizarCampo(tf);
        }
        estilizarCampo(observacoes);
        status.setFont(Fontes.regular(13f));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(18, 20, 8, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 4, 5, 4);
        g.anchor = GridBagConstraints.WEST;
        int linha = 0;

        linha = addLinha(form, g, linha, "Nome*:", nome);
        linha = addLinha(form, g, linha, "CPF/CNPJ*:", cpfCnpj);
        linha = addLinha(form, g, linha, "E-mail:", email);
        linha = addLinha(form, g, linha, "Telefone:", telefone);

        JPanel enderecoLinha1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        enderecoLinha1.add(new JLabel("Rua:"));
        enderecoLinha1.add(logradouro);
        enderecoLinha1.add(new JLabel("Nro:"));
        enderecoLinha1.add(numero);
        linha = addLinha(form, g, linha, "Endereco:", enderecoLinha1);

        JPanel enderecoLinha2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        enderecoLinha2.add(new JLabel("Bairro:"));
        enderecoLinha2.add(bairro);
        enderecoLinha2.add(new JLabel("Cidade:"));
        enderecoLinha2.add(cidade);
        enderecoLinha2.add(new JLabel("UF:"));
        enderecoLinha2.add(uf);
        enderecoLinha2.add(new JLabel("CEP:"));
        enderecoLinha2.add(cep);
        linha = addLinha(form, g, linha, "", enderecoLinha2);

        linha = addLinha(form, g, linha, "Status:", status);
        linha = addLinha(form, g, linha, "Valor a receber:", valorAReceber);
        linha = addLinha(form, g, linha, "Vencimento (dd/MM/aaaa):", dataVencimento);

        observacoes.setLineWrap(true);
        observacoes.setWrapStyleWord(true);
        linha = addLinha(form, g, linha, "Observacoes:", new JScrollPane(observacoes));

        BotaoModerno salvar = new BotaoModerno("Salvar cliente", null, BotaoModerno.Tipo.PRIMARIO);
        salvar.addActionListener(e -> aoSalvar());
        BotaoModerno cancelar = new BotaoModerno("Cancelar", null, BotaoModerno.Tipo.GHOST);
        cancelar.addActionListener(e -> dispose());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        botoes.setOpaque(false);
        botoes.setBorder(new EmptyBorder(6, 12, 10, 16));
        botoes.add(cancelar);
        botoes.add(salvar);

        getRootPane().setDefaultButton(salvar);
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Cores.FUNDO);
        raiz.add(construirCabecalho(), BorderLayout.NORTH);
        raiz.add(form, BorderLayout.CENTER);
        raiz.add(botoes, BorderLayout.SOUTH);
        setContentPane(raiz);
    }

    private JComponent construirCabecalho() {
        boolean novo = cliente.getId() == null;
        JLabel icone = new JLabel(Icones.de(novo ? Icones.MAIS : Icones.LAPIS, 18, Cores.ACENTO));
        icone.setBorder(new EmptyBorder(0, 0, 0, 10));
        JLabel titulo = new JLabel(novo ? "Novo cliente" : "Editar cliente");
        titulo.setFont(Fontes.semibold(16f));
        titulo.setForeground(Cores.TINTA);

        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        painel.setBackground(Cores.SUPERFICIE);
        painel.setBorder(new CompoundBorder(
                new javax.swing.border.MatteBorder(0, 0, 1, 0, Cores.BORDA),
                new EmptyBorder(16, 20, 16, 20)));
        painel.add(icone);
        painel.add(titulo);
        return painel;
    }

    private void estilizarCampo(JTextComponent campo) {
        campo.setFont(Fontes.regular(13f));
        campo.setForeground(Cores.TINTA);
        campo.setCaretColor(Cores.ACENTO);
        campo.setBorder(new CompoundBorder(
                new LineBorder(Cores.BORDA_FORTE, 1, true),
                new EmptyBorder(6, 8, 6, 8)));
    }

    private int addLinha(JPanel form, GridBagConstraints g, int linha, String rotulo, Component campo) {
        g.gridx = 0;
        g.gridy = linha;
        g.fill = GridBagConstraints.NONE;
        JLabel l = new JLabel(rotulo);
        l.setFont(Fontes.regular(12.5f));
        l.setForeground(Cores.TINTA_SUAVE);
        form.add(l, g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        form.add(campo, g);
        g.weightx = 0;
        return linha + 1;
    }

    private void preencherCampos() {
        nome.setText(nvl(cliente.getNome()));
        cpfCnpj.setText(nvl(cliente.getCpfCnpj()));
        email.setText(nvl(cliente.getEmail()));
        telefone.setText(nvl(cliente.getTelefone()));
        Endereco end = cliente.getEndereco();
        logradouro.setText(nvl(end.getLogradouro()));
        numero.setText(nvl(end.getNumero()));
        bairro.setText(nvl(end.getBairro()));
        cidade.setText(nvl(end.getCidade()));
        uf.setText(nvl(end.getUf()));
        cep.setText(nvl(end.getCep()));
        status.setSelectedItem(cliente.getStatus());
        if (cliente.temValorAReceber()) {
            valorAReceber.setText(cliente.getValorAReceber().toPlainString().replace(".", ","));
        }
        dataVencimento.setText(Formatos.data(cliente.getDataVencimento()));
        observacoes.setText(nvl(cliente.getObservacoes()));
    }

    private void aoSalvar() {
        try {
            cliente.setNome(nome.getText());
            cliente.setCpfCnpj(cpfCnpj.getText());
            cliente.setEmail(email.getText());
            cliente.setTelefone(telefone.getText());

            Endereco end = cliente.getEndereco();
            end.setLogradouro(logradouro.getText());
            end.setNumero(numero.getText());
            end.setBairro(bairro.getText());
            end.setCidade(cidade.getText());
            end.setUf(uf.getText().trim().toUpperCase());
            end.setCep(cep.getText());

            cliente.setStatus((StatusCliente) status.getSelectedItem());
            cliente.setObservacoes(observacoes.getText());

            try {
                cliente.setValorAReceber(Formatos.parseValor(valorAReceber.getText()));
            } catch (NumberFormatException ex) {
                mostrarErro("Valor a receber invalido. Use algo como 1234,56.");
                return;
            }
            try {
                cliente.setDataVencimento(Formatos.parseData(dataVencimento.getText()));
            } catch (DateTimeParseException ex) {
                mostrarErro("Data de vencimento invalida. Use o formato dd/MM/aaaa.");
                return;
            }

            service.salvar(cliente);
            salvo = true;
            dispose();
        } catch (ValidacaoException ex) {
            mostrarErro(String.join("\n", ex.getErros()));
        }
    }

    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Dados invalidos", JOptionPane.WARNING_MESSAGE);
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }
}
