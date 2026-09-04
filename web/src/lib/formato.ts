const fmtMoeda = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });

export function moeda(n: number | null | undefined): string {
  return fmtMoeda.format(n || 0);
}

export function dataBR(iso: string | null | undefined): string {
  if (!iso) return "—";
  const [ano, mes, dia] = iso.split("-");
  return `${dia}/${mes}/${ano}`;
}

/** "1.234,56" | "1234.56" | "1234,56" -> 1234.56 (NaN se inválido) */
export function parseValor(texto: string): number {
  if (!texto || !texto.trim()) return 0;
  const limpo = texto
    .trim()
    .replace(/r\$/i, "")
    .replace(/\s/g, "")
    .replace(/\./g, "")
    .replace(",", ".");
  return Number(limpo);
}
