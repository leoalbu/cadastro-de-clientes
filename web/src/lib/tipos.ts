export type StatusCliente = "ATIVO" | "INATIVO";

export type SituacaoFinanceira = "EM_DIA" | "A_VENCER" | "VENCE_HOJE" | "VENCIDO";

/** Forma como o cliente circula na aplicação (camelCase). */
export interface Cliente {
  id: number;
  nome: string;
  cpfCnpj: string;
  email: string | null;
  telefone: string | null;
  cidade: string | null;
  uf: string | null;
  status: StatusCliente;
  observacoes: string | null;
  valorAReceber: number;
  dataVencimento: string | null; // "yyyy-MM-dd"
  dataCadastro: string | null; // "yyyy-MM-dd"
}

/** Forma como a linha vem/vai para a tabela `clientes` no Supabase (snake_case). */
export interface LinhaCliente {
  id: number;
  nome: string;
  cpf_cnpj: string;
  email: string | null;
  telefone: string | null;
  cidade: string | null;
  uf: string | null;
  status: StatusCliente;
  observacoes: string | null;
  valor_a_receber: number;
  data_vencimento: string | null;
  data_cadastro: string | null;
}

export function daLinha(l: LinhaCliente): Cliente {
  return {
    id: l.id,
    nome: l.nome,
    cpfCnpj: l.cpf_cnpj,
    email: l.email,
    telefone: l.telefone,
    cidade: l.cidade,
    uf: l.uf,
    status: l.status,
    observacoes: l.observacoes,
    valorAReceber: Number(l.valor_a_receber ?? 0),
    dataVencimento: l.data_vencimento,
    dataCadastro: l.data_cadastro,
  };
}

export interface FiltroClientes {
  texto?: string;
  status?: StatusCliente;
  situacao?: SituacaoFinanceira;
  venceEmDias?: number;
}

export const LABEL_SITUACAO: Record<SituacaoFinanceira, string> = {
  EM_DIA: "Em dia",
  A_VENCER: "A vencer",
  VENCE_HOJE: "Vence hoje",
  VENCIDO: "Vencido",
};
