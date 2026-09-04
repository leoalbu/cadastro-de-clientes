import { supabase, TABELA_CLIENTES } from "./supabase";
import { Cliente, daLinha, FiltroClientes, LinhaCliente } from "./tipos";
import { somenteDigitos } from "./validacao";
import { diasAteVencimento, hojeISO, situacaoFinanceira, temValor } from "./situacao";

/** Busca todos os clientes e aplica os filtros em memória (dataset pequeno, estudo). */
export async function listarClientes(filtro: FiltroClientes = {}): Promise<Cliente[]> {
  const { data, error } = await supabase
    .from(TABELA_CLIENTES)
    .select("*")
    .order("nome", { ascending: true });

  if (error) throw new Error(`Falha ao buscar clientes: ${error.message}`);

  let clientes = (data as LinhaCliente[]).map(daLinha);
  const hoje = hojeISO();

  if (filtro.texto) {
    const texto = filtro.texto.toLowerCase();
    const textoDigitos = somenteDigitos(filtro.texto);
    clientes = clientes.filter((c) => {
      const porNome = c.nome.toLowerCase().includes(texto);
      const porDoc = textoDigitos.length > 0 && c.cpfCnpj.includes(textoDigitos);
      return porNome || porDoc;
    });
  }
  if (filtro.status) {
    clientes = clientes.filter((c) => c.status === filtro.status);
  }
  if (filtro.situacao) {
    clientes = clientes.filter((c) => situacaoFinanceira(c, hoje) === filtro.situacao);
  }
  if (filtro.venceEmDias !== undefined) {
    clientes = clientes.filter((c) => {
      if (!temValor(c) || !c.dataVencimento) return false;
      const dias = diasAteVencimento(c, hoje);
      return dias !== null && dias >= 0 && dias <= filtro.venceEmDias!;
    });
  }
  return clientes;
}

export async function buscarClientePorId(id: number): Promise<Cliente | null> {
  const { data, error } = await supabase
    .from(TABELA_CLIENTES)
    .select("*")
    .eq("id", id)
    .maybeSingle();
  if (error) throw new Error(`Falha ao buscar cliente: ${error.message}`);
  return data ? daLinha(data as LinhaCliente) : null;
}

export async function existeCpfCnpj(cpfCnpj: string, idIgnorar?: number): Promise<boolean> {
  const doc = somenteDigitos(cpfCnpj);
  if (!doc) return false;
  let query = supabase.from(TABELA_CLIENTES).select("id").eq("cpf_cnpj", doc);
  if (idIgnorar) query = query.neq("id", idIgnorar);
  const { data, error } = await query;
  if (error) throw new Error(`Falha ao checar CPF/CNPJ: ${error.message}`);
  return (data?.length ?? 0) > 0;
}

export interface DadosCliente {
  id?: number;
  nome: string;
  cpfCnpj: string;
  email?: string;
  telefone?: string;
  cidade?: string;
  uf?: string;
  status: "ATIVO" | "INATIVO";
  observacoes?: string;
  valorAReceber: number;
  dataVencimento?: string | null;
}

function paraLinha(d: DadosCliente) {
  return {
    nome: d.nome.trim(),
    cpf_cnpj: somenteDigitos(d.cpfCnpj),
    email: d.email?.trim() || null,
    telefone: d.telefone?.trim() || null,
    cidade: d.cidade?.trim() || null,
    uf: d.uf?.trim().toUpperCase() || null,
    status: d.status,
    observacoes: d.observacoes?.trim() || null,
    valor_a_receber: d.valorAReceber,
    data_vencimento: d.dataVencimento || null,
  };
}

/** Cria (sem `id`) ou atualiza (com `id`) um cliente. Devolve o registro salvo. */
export async function salvarCliente(d: DadosCliente): Promise<Cliente> {
  const linha = paraLinha(d);

  if (d.id) {
    const { data, error } = await supabase
      .from(TABELA_CLIENTES)
      .update(linha)
      .eq("id", d.id)
      .select()
      .single();
    if (error) throw new Error(`Falha ao atualizar cliente: ${error.message}`);
    return daLinha(data as LinhaCliente);
  }

  const { data, error } = await supabase
    .from(TABELA_CLIENTES)
    .insert(linha)
    .select()
    .single();
  if (error) throw new Error(`Falha ao criar cliente: ${error.message}`);
  return daLinha(data as LinhaCliente);
}

export async function excluirCliente(id: number): Promise<void> {
  const { error } = await supabase.from(TABELA_CLIENTES).delete().eq("id", id);
  if (error) throw new Error(`Falha ao excluir cliente: ${error.message}`);
}

export async function alternarStatusCliente(id: number, statusAtual: "ATIVO" | "INATIVO") {
  const novo = statusAtual === "ATIVO" ? "INATIVO" : "ATIVO";
  const { error } = await supabase.from(TABELA_CLIENTES).update({ status: novo }).eq("id", id);
  if (error) throw new Error(`Falha ao alterar status: ${error.message}`);
}
