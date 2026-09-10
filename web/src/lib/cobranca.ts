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
  whatsapp:
    "mensagem de WhatsApp: curta (2 a 4 frases), pode usar 1 emoji discreto, sem linha de assunto e sem assinatura",
  email:
    "e-mail: primeira linha 'Assunto: ...', depois saudação, corpo e encerre apenas com 'Atenciosamente,' — sem nome, empresa, telefone ou qualquer assinatura",
};

/** Remove placeholders entre colchetes e assinaturas vazias que o modelo às vezes inventa. */
function limparTexto(texto: string): string {
  return texto
    .split(/\r?\n/)
    .filter((linha) => !/^\s*\[[^\]]*\]\s*$/.test(linha)) // linha que é só "[Algo]"
    .join("\n")
    .replace(/\[[^\]]*\]/g, "") // colchetes restantes no meio de frases
    .replace(/[ \t]+\n/g, "\n")
    .replace(/\n{3,}/g, "\n\n")
    .trim();
}

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
    "Escreva apenas o texto final da mensagem, pronto para enviar, sem comentários seus e sem aspas.",
    "NUNCA use colchetes nem placeholders como [Seu Nome], [Empresa], [Telefone].",
    "Não invente valores, datas, formas de pagamento, links, nomes de pessoas ou de empresa que não foram fornecidos.",
    "Não escreva bloco de assinatura com dados de contato; no máximo termine com 'Atenciosamente,'.",
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

  const texto = await chamarIA({ system, user, temperatura: 0.7, maxTokens: 400 });
  return limparTexto(texto);
}
