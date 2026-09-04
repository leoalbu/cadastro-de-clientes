import { Cliente, SituacaoFinanceira } from "./tipos";

export function hojeISO(): string {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(
    d.getDate()
  ).padStart(2, "0")}`;
}

export function temValor(c: Pick<Cliente, "valorAReceber">): boolean {
  return (c.valorAReceber || 0) > 0;
}

export function diffDias(isoAlvo: string, isoRef: string): number {
  const a = new Date(isoAlvo + "T00:00:00");
  const b = new Date(isoRef + "T00:00:00");
  return Math.round((a.getTime() - b.getTime()) / 86400000);
}

export function situacaoFinanceira(
  c: Pick<Cliente, "valorAReceber" | "dataVencimento">,
  referencia = hojeISO()
): SituacaoFinanceira {
  if (!temValor(c) || !c.dataVencimento) return "EM_DIA";
  const d = diffDias(c.dataVencimento, referencia);
  if (d < 0) return "VENCIDO";
  if (d === 0) return "VENCE_HOJE";
  return "A_VENCER";
}

export function diasAteVencimento(
  c: Pick<Cliente, "valorAReceber" | "dataVencimento">,
  referencia = hojeISO()
): number | null {
  if (!temValor(c) || !c.dataVencimento) return null;
  return diffDias(c.dataVencimento, referencia);
}
