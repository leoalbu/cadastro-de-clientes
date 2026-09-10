/**
 * Chamada de LLM via OpenRouter (API compatível com a da OpenAI).
 * Só roda no servidor: usa `process.env.OPENROUTER_API_KEY`, que nunca vai
 * para o navegador. Configure em `web/.env.local` (veja `web/.env.example`).
 */

const ENDPOINT = "https://openrouter.ai/api/v1/chat/completions";
const MODELO_PADRAO = "openai/gpt-4o-mini";

export interface OpcoesIA {
  system: string;
  user: string;
  /** 0 a 2. Menor = mais previsível. Padrão 0.7. */
  temperatura?: number;
  /** Teto de tokens da resposta. Padrão 500. */
  maxTokens?: number;
}

export async function chamarIA(opcoes: OpcoesIA): Promise<string> {
  const chave = process.env.OPENROUTER_API_KEY;
  if (!chave) {
    throw new Error(
      "IA não configurada: defina OPENROUTER_API_KEY em web/.env.local (e nas Variables do Railway)."
    );
  }

  const modelo = process.env.OPENROUTER_MODEL || MODELO_PADRAO;

  const resposta = await fetch(ENDPOINT, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${chave}`,
      "Content-Type": "application/json",
      "X-Title": "Cadastro de Clientes",
    },
    body: JSON.stringify({
      model: modelo,
      temperature: opcoes.temperatura ?? 0.7,
      max_tokens: opcoes.maxTokens ?? 500,
      messages: [
        { role: "system", content: opcoes.system },
        { role: "user", content: opcoes.user },
      ],
    }),
    cache: "no-store",
  });

  if (!resposta.ok) {
    const corpo = await resposta.text().catch(() => "");
    throw new Error(`OpenRouter respondeu HTTP ${resposta.status}. ${corpo}`.trim());
  }

  const dados = (await resposta.json()) as {
    choices?: { message?: { content?: string } }[];
  };
  const texto = dados.choices?.[0]?.message?.content?.trim();
  if (!texto) {
    throw new Error("A IA não retornou nenhum texto.");
  }
  return texto;
}
