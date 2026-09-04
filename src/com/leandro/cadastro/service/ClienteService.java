package com.leandro.cadastro.service;

import com.leandro.cadastro.exception.ValidacaoException;
import com.leandro.cadastro.model.Cliente;
import com.leandro.cadastro.model.SituacaoFinanceira;
import com.leandro.cadastro.model.StatusCliente;
import com.leandro.cadastro.repository.ClienteRepository;
import com.leandro.cadastro.util.ValidadorCpfCnpj;
import com.leandro.cadastro.util.ValidadorEmail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Regras de negocio do cadastro de clientes. A interface (Swing) fala apenas
 * com esta classe, nunca direto com o repositorio.
 */
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    // ----------------------------------------------------------------- CRUD

    /**
     * Insere ou atualiza um cliente depois de validar os dados.
     *
     * @throws ValidacaoException se algum campo estiver invalido
     */
    public Cliente salvar(Cliente cliente) {
        validar(cliente);
        // normaliza o CPF/CNPJ para so digitos antes de guardar
        cliente.setCpfCnpj(ValidadorCpfCnpj.somenteDigitos(cliente.getCpfCnpj()));
        if (cliente.getEmail() != null) {
            cliente.setEmail(cliente.getEmail().trim());
        }
        cliente.setNome(cliente.getNome().trim());
        return repository.salvar(cliente);
    }

    public Cliente buscarPorId(long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente " + id + " nao encontrado"));
    }

    public List<Cliente> listarTodos() {
        return repository.buscarTodos();
    }

    public void excluir(long id) {
        if (!repository.remover(id)) {
            throw new NoSuchElementException("Cliente " + id + " nao encontrado");
        }
    }

    /** Marca o cliente como INATIVO sem apagar o registro. */
    public Cliente inativar(long id) {
        Cliente c = buscarPorId(id);
        c.setStatus(StatusCliente.INATIVO);
        return repository.salvar(c);
    }

    public Cliente reativar(long id) {
        Cliente c = buscarPorId(id);
        c.setStatus(StatusCliente.ATIVO);
        return repository.salvar(c);
    }

    // ------------------------------------------------------------- Validacao

    private void validar(Cliente c) {
        List<String> erros = new ArrayList<>();

        if (c.getNome() == null || c.getNome().isBlank()) {
            erros.add("Nome e obrigatorio.");
        }

        if (c.getCpfCnpj() == null || c.getCpfCnpj().isBlank()) {
            erros.add("CPF/CNPJ e obrigatorio.");
        } else if (!ValidadorCpfCnpj.valido(c.getCpfCnpj())) {
            erros.add("CPF/CNPJ invalido.");
        } else if (repository.existeCpfCnpj(c.getCpfCnpj(), c.getId())) {
            erros.add("Ja existe um cliente com este CPF/CNPJ.");
        }

        if (c.getEmail() != null && !c.getEmail().isBlank()
                && !ValidadorEmail.valido(c.getEmail())) {
            erros.add("E-mail em formato invalido.");
        }

        if (c.getValorAReceber() != null
                && c.getValorAReceber().compareTo(BigDecimal.ZERO) < 0) {
            erros.add("Valor a receber nao pode ser negativo.");
        }

        if (c.getValorAReceber() != null
                && c.getValorAReceber().compareTo(BigDecimal.ZERO) > 0
                && c.getDataVencimento() == null) {
            erros.add("Informe a data de vencimento quando houver valor a receber.");
        }

        if (!erros.isEmpty()) {
            throw new ValidacaoException(erros);
        }
    }

    // --------------------------------------------------------------- Filtros

    /**
     * Aplica os criterios de {@link FiltroClientes} sobre todos os clientes.
     * A lista volta ordenada por nome.
     */
    public List<Cliente> filtrar(FiltroClientes filtro) {
        LocalDate hoje = LocalDate.now();
        Predicate<Cliente> predicado = montarPredicado(filtro, hoje);
        return repository.buscarTodos().stream()
                .filter(predicado)
                .sorted(Comparator.comparing(c -> c.getNome().toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    private Predicate<Cliente> montarPredicado(FiltroClientes filtro, LocalDate hoje) {
        Predicate<Cliente> p = c -> true;
        if (filtro == null || filtro.vazio()) {
            return p;
        }

        if (filtro.getTexto() != null) {
            String alvo = filtro.getTexto().toLowerCase(Locale.ROOT);
            String alvoDigitos = ValidadorCpfCnpj.somenteDigitos(filtro.getTexto());
            p = p.and(c -> {
                boolean porNome = c.getNome() != null
                        && c.getNome().toLowerCase(Locale.ROOT).contains(alvo);
                boolean porDoc = !alvoDigitos.isEmpty()
                        && ValidadorCpfCnpj.somenteDigitos(c.getCpfCnpj()).contains(alvoDigitos);
                return porNome || porDoc;
            });
        }

        if (filtro.getStatus() != null) {
            p = p.and(c -> c.getStatus() == filtro.getStatus());
        }

        if (filtro.getSituacao() != null) {
            p = p.and(c -> c.getSituacaoFinanceira(hoje) == filtro.getSituacao());
        }

        if (filtro.getVenceEmDias() != null) {
            int dias = filtro.getVenceEmDias();
            p = p.and(c -> {
                if (!c.temValorAReceber() || c.getDataVencimento() == null) {
                    return false;
                }
                Long faltam = c.diasAteVencimento(hoje);
                return faltam != null && faltam >= 0 && faltam <= dias;
            });
        }

        return p;
    }

    /** Atalho: clientes com titulo vencido hoje. */
    public List<Cliente> listarVencidos() {
        return filtrar(new FiltroClientes().setSituacao(SituacaoFinanceira.VENCIDO));
    }

    // ---------------------------------------------------------------- Resumo

    public ResumoFinanceiro resumo(List<Cliente> clientes) {
        return ResumoFinanceiro.de(clientes, LocalDate.now());
    }
}
