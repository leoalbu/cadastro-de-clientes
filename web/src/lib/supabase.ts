import { createClient } from "@supabase/supabase-js";

/**
 * Cliente Supabase usado só no servidor (Server Components / Server Actions).
 * As variáveis não têm prefixo NEXT_PUBLIC_ de propósito: nunca vão para o
 * bundle do navegador. Configure em `.env.local` (veja `.env.example`).
 */
function criarCliente() {
  const url = process.env.SUPABASE_URL;
  const chave = process.env.SUPABASE_ANON_KEY;

  if (!url || !chave) {
    throw new Error(
      "Supabase não configurado: defina SUPABASE_URL e SUPABASE_ANON_KEY em web/.env.local (veja web/.env.example)."
    );
  }

  return createClient(url, chave, {
    auth: { persistSession: false },
  });
}

export const supabase = criarCliente();

export const TABELA_CLIENTES = process.env.SUPABASE_TABLE_CLIENTES || "clientes";
