import { Cliente } from "./tipos";
import { chamarIA } from "./ia";
import { diasAteVencimento, situacaoFinanceira } from "./situacao";
import { moeda, dataBR } from "./formato";

export type TomCobranca = "amigavel" | "neutro" | "firme";
export type CanalCobranca = "whatsapp" | "email";

const DESCRICAO_TOM: Record<TomCobranca, string> = {
  amigavel: "cordial e leve, como um lembrete gentil entre parceiros",
  neutro: "profissional e objetivo, sem rodeios e sem frieza",
  firme: "educado porém firme, deixando claro que o pagamento está atrasado e é esperado",
};

const DESCRICAO_CANAL: Record<CanalCobranca, string> = {
  whatsapp: "mensagem de WhatsApp: curta (2 a 4 frases), pode usar 1 emoji discreto, sem assunto",
  email: "e-mail: comece com uma linha 'Assunto: ...', depois saudação, corpo e assinatura genérica",
};

/** Monta o prompt e chama a IA para gerar o texto de cobrança de um cliente. */
export async function gerarTextoCobranca(
  cliente: Cliente,
  tom: TomCobranca,
  canal: CanalCobranca
): Promise<string> {
  const situacao = situacaoFinanceira(cliente);
  const dias = diasAteVencimento(cliente);

  let contextoPrazo: string;
  if (situacao === "VENCIDO" && dias !== null) {
    contextoPrazo = `A cobrança está VENCIDA há ${Math.abs(dias)} dia(s) (venceu em ${dataBR(
      cliente.dataVencimento
    )}).`;
  } else if (situacao === "VENCE_HOJE") {
    contextoPrazo = `A cobrança vence HOJE (${dataBR(cliente.dataVencimento)}).`;
  } else if (situacao === "A_VENCER" && dias !== null) {
    contextoPrazo = `A cobrança ainda vai vencer em ${dias} dia(s) (${dataBR(
      cliente.dataVencimento
    )}). Trate como lembrete preventivo, não como atraso.`;
  } else {
    contextoPrazo = "Não há data de vencimento definida; trate como lembrete geral de pendência.";
  }

  const system = [
    "Você escreve mensagens de cobrança em português do Brasil para uma pequena empresa.",
    "Escreva apenas o texto final da mensagem, pronto para enviar, sem comentários seus, sem aspas e sem placeholders entre colchetes.",
    "Não invente valores, datas, formas de pagamento, links ou dados que não foram fornecidos.",
    "Use o primeiro nome do cliente na saudação quando fizer sentido.",
    "Seja respeitoso: o objetivo é receber o pagamento preservando o relacionamento.",
  ].join(" ");

  const user = [
    `Cliente: ${cliente.nome}`,
    `Valor a receber: ${moeda(cliente.valorAReceber)}`,
    contextoPrazo,
    `Tom desejado: ${DESCRICAO_TOM[tom]}.`,
    `Formato: ${DESCRICAO_CANAL[canal]}.`,
    "Peça de forma clara a regularização e ofereça abertura para conversar em caso de dúvida.",
  ].join("\n");

  return chamarIA({ system, user, temperatura: 0.7, maxTokens: 400 });
}
