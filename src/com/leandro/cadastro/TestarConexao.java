package com.leandro.cadastro;

import com.leandro.cadastro.model.Cliente;
import com.leandro.cadastro.repository.ClienteRepositorySupabase;

import java.util.List;

/**
 * Ferramenta de linha de comando para checar a conexao com o Supabase sem
 * abrir a interface grafica.
 *
 * <pre>  java -cp bin com.leandro.cadastro.TestarConexao</pre>
 */
public class TestarConexao {

    public static void main(String[] args) {
        try {
            ClienteRepositorySupabase repo = new ClienteRepositorySupabase();
            System.out.println("Verificando conexao com o Supabase...");
            repo.verificarConexao();
            List<Cliente> clientes = repo.buscarTodos();
            System.out.println("OK! Conexao e tabela 'clientes' funcionando.");
            System.out.println("Registros encontrados: " + clientes.size());
            for (Cliente c : clientes) {
                System.out.println("  - " + c.getNome() + " (" + c.getCpfCnpj() + ")");
            }
        } catch (RuntimeException e) {
            System.out.println();
            System.out.println("FALHOU: " + e.getMessage());
            System.out.println();
            System.out.println("Confira: (1) SUPABASE_URL e SUPABASE_ANON_KEY no .env;");
            System.out.println("         (2) se voce rodou o db/schema.sql no SQL Editor;");
            System.out.println("         (3) se a policy de RLS permite acesso ao papel anon.");
            System.exit(1);
        }
    }
}
