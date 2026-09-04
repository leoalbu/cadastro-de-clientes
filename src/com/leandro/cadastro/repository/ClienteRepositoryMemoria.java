package com.leandro.cadastro.repository;

import com.leandro.cadastro.model.Cliente;
import com.leandro.cadastro.util.ValidadorCpfCnpj;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementacao em memoria: os dados vivem enquanto o programa esta aberto.
 * Usa {@link LinkedHashMap} para preservar a ordem de cadastro.
 */
public class ClienteRepositoryMemoria implements ClienteRepository {

    private final Map<Long, Cliente> dados = new LinkedHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(0);

    @Override
    public Cliente salvar(Cliente cliente) {
        if (cliente.getId() == null) {
            cliente.setId(sequencia.incrementAndGet());
        }
        dados.put(cliente.getId(), cliente);
        return cliente;
    }

    @Override
    public Optional<Cliente> buscarPorId(long id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Cliente> buscarTodos() {
        return new ArrayList<>(dados.values());
    }

    @Override
    public boolean remover(long id) {
        return dados.remove(id) != null;
    }

    @Override
    public boolean existeCpfCnpj(String cpfCnpj, Long idIgnorar) {
        String alvo = ValidadorCpfCnpj.somenteDigitos(cpfCnpj);
        if (alvo.isEmpty()) {
            return false;
        }
        return dados.values().stream()
                .filter(c -> idIgnorar == null || !idIgnorar.equals(c.getId()))
                .anyMatch(c -> alvo.equals(ValidadorCpfCnpj.somenteDigitos(c.getCpfCnpj())));
    }
}
