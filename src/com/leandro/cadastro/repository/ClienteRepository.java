package com.leandro.cadastro.repository;

import com.leandro.cadastro.model.Cliente;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de armazenamento de clientes. A implementacao atual guarda tudo em
 * memoria ({@link ClienteRepositoryMemoria}); para persistir em arquivo ou
 * banco de dados basta criar outra classe que implemente esta interface.
 */
public interface ClienteRepository {

    /**
     * Salva um cliente. Se {@code cliente.getId()} for {@code null}, gera um id
     * novo (insercao); caso contrario atualiza o existente.
     *
     * @return o proprio cliente, agora com id preenchido
     */
    Cliente salvar(Cliente cliente);

    Optional<Cliente> buscarPorId(long id);

    /** Todos os clientes, na ordem de insercao. */
    List<Cliente> buscarTodos();

    /** Remove pelo id. @return {@code true} se algo foi removido. */
    boolean remover(long id);

    /**
     * Verifica se ja existe outro cliente com o mesmo CPF/CNPJ (comparando
     * somente os digitos). O {@code idIgnorar} permite pular o proprio cliente
     * durante uma edicao (use {@code null} numa insercao).
     */
    boolean existeCpfCnpj(String cpfCnpj, Long idIgnorar);
}
