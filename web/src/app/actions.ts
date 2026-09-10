"use server";

import { revalidatePath } from "next/cache";
import {
  alternarStatusCliente,
  buscarClientePorId,
  DadosCliente,
  excluirCliente,
  existeCpfCnpj,
  salvarCliente,
} from "@/lib/clientes";
import { docValido, emailValido } from "@/lib/validacao";
import { Cliente } from "@/lib/tipos";
import { CanalCobranca, gerarTextoCobranca, TomCobranca } from "@/lib/cobranca";

export interface ResultadoAcao {
  ok: boolean;
  erros?: string[];
  cliente?: Cliente;
}

/** Mesmas regras de negócio do app Java (ClienteService.validar) e da simulação web. */
async function validar(d: DadosCliente): Promise<string[]> {
  const erros: string[] = [];

  if (!d.nome || !d.nome.trim()) erros.push("Nome é obrigatório.");

  if (!d.cpfCnpj || !d.cpfCnpj.trim()) {
    erros.push("CPF/CNPJ é obrigatório.");
  } else if (!docValido(d.cpfCnpj)) {
    erros.push("CPF/CNPJ inválido.");
  } else if (await existeCpfCnpj(d.cpfCnpj, d.id)) {
    erros.push("Já existe um cliente com este CPF/CNPJ.");
  }

  if (d.email && d.email.trim() && !emailValido(d.email)) {
    erros.push("E-mail em formato inválido.");
  }

  if (d.valorAReceber < 0 || Number.isNaN(d.valorAReceber)) {
    erros.push("Valor a receber inválido.");
  } else if (d.valorAReceber > 0 && !d.dataVencimento) {
    erros.push("Informe a data de vencimento quando houver valor a receber.");
  }

  return erros;
}

export async function salvarClienteAction(dados: DadosCliente): Promise<ResultadoAcao> {
  const erros = await validar(dados);
  if (erros.length > 0) {
    return { ok: false, erros };
  }
  try {
    const cliente = await salvarCliente(dados);
    revalidatePath("/");
    return { ok: true, cliente };
  } catch (e) {
    return { ok: false, erros: [(e as Error).message] };
  }
}

export async function excluirClienteAction(id: number): Promise<ResultadoAcao> {
  try {
    await excluirCliente(id);
    revalidatePath("/");
    return { ok: true };
  } catch (e) {
    return { ok: false, erros: [(e as Error).message] };
  }
}

export async function alternarStatusAction(
  id: number,
  statusAtual: "ATIVO" | "INATIVO"
): Promise<ResultadoAcao> {
  try {
    await alternarStatusCliente(id, statusAtual);
    revalidatePath("/");
    return { ok: true };
  } catch (e) {
    return { ok: false, erros: [(e as Error).message] };
  }
}

export interface ResultadoCobranca {
  ok: boolean;
  texto?: string;
  erro?: string;
}

/** Gera, via IA (OpenRouter), um texto de cobrança para o cliente. */
export async function gerarCobrancaAction(
  clienteId: number,
  tom: TomCobranca,
  canal: CanalCobranca
): Promise<ResultadoCobranca> {
  try {
    const cliente = await buscarClientePorId(clienteId);
    if (!cliente) {
      return { ok: false, erro: "Cliente não encontrado." };
    }
    if (cliente.valorAReceber <= 0) {
      return { ok: false, erro: "Este cliente não tem valor a receber." };
    }
    const texto = await gerarTextoCobranca(cliente, tom, canal);
    return { ok: true, texto };
  } catch (e) {
    return { ok: false, erro: (e as Error).message };
  }
}
