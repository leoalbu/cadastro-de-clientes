"use client";

import { useMemo } from "react";
import { Cliente } from "@/lib/tipos";
import { diasAteVencimento, situacaoFinanceira } from "@/lib/situacao";
import { dataBR, moeda } from "@/lib/formato";
import { formatarDoc } from "@/lib/validacao";
import { Badge } from "./Badge";
import { IconeLapis, IconeLixeira, IconeReciclar } from "./Icones";

const LABEL_SITUACAO = {
  EM_DIA: "Em dia",
  A_VENCER: "A vencer",
  VENCE_HOJE: "Vence hoje",
  VENCIDO: "Vencido",
} as const;

export function PainelClientes({
  clientes,
  selecionadoId,
  processando,
  onSelecionar,
  onAbrirEdicao,
  onEditar,
  onExcluir,
  onAlternarStatus,
}: {
  clientes: Cliente[];
  selecionadoId: number | null;
  processando: boolean;
  onSelecionar: (id: number) => void;
  onAbrirEdicao: () => void;
  onEditar: () => void;
  onExcluir: () => void;
  onAlternarStatus: () => void;
}) {
  const selecionado = clientes.find((c) => c.id === selecionadoId) ?? null;

  const resumo = useMemo(() => {
    let total = 0;
    let totalVencido = 0;
    let qtdVencidos = 0;
    for (const c of clientes) {
      total += c.valorAReceber;
      if (situacaoFinanceira(c) === "VENCIDO") {
        totalVencido += c.valorAReceber;
        qtdVencidos++;
      }
    }
    return { total, totalVencido, qtdVencidos, quantidade: clientes.length };
  }, [clientes]);

  return (
    <div className="flex flex-col gap-3.5">
      <div className="overflow-hidden rounded-xl border border-borda bg-superficie shadow-sm">
        <div className="max-h-[60vh] overflow-auto">
          <table className="w-full border-collapse text-sm">
            <thead className="sticky top-0 bg-superficie-2">
              <tr>
                {["Nome", "CPF/CNPJ", "Telefone", "Cidade/UF", "Valor a Receber", "Vencimento", "Situação", "Status"].map(
                  (col, i) => (
                    <th
                      key={col}
                      className={`whitespace-nowrap px-4 py-3 text-[11px] font-semibold uppercase tracking-wide text-tinta-suave ${
                        i === 4 ? "text-right" : "text-left"
                      }`}
                    >
                      {col}
                    </th>
                  )
                )}
              </tr>
            </thead>
            <tbody>
              {clientes.length === 0 && (
                <tr>
                  <td colSpan={8} className="px-4 py-14 text-center text-tinta-fraca">
                    Nenhum cliente encontrado com esses filtros.
                  </td>
                </tr>
              )}
              {clientes.map((c, i) => {
                const sit = situacaoFinanceira(c);
                const dias = diasAteVencimento(c);
                const proximo = sit === "VENCE_HOJE" || (dias !== null && dias >= 0 && dias <= 7);
                const faixa =
                  sit === "VENCIDO" ? "border-l-vencido" : proximo ? "border-l-proximo" : "border-l-transparent";
                const inativo = c.status === "INATIVO";

                return (
                  <tr
                    key={c.id}
                    onClick={() => onSelecionar(c.id)}
                    onDoubleClick={() => {
                      onSelecionar(c.id);
                      onAbrirEdicao();
                    }}
                    className={`cursor-pointer border-l-[3px] ${faixa} ${
                      selecionadoId === c.id ? "bg-acento-suave" : i % 2 === 0 ? "bg-superficie" : "bg-fundo"
                    } ${inativo ? "text-tinta-fraca" : "text-tinta"} transition-colors hover:bg-acento-suave/60`}
                  >
                    <td className="px-4 py-2.5 font-medium">{c.nome}</td>
                    <td className="whitespace-nowrap px-4 py-2.5 font-mono text-xs text-tinta-suave">
                      {formatarDoc(c.cpfCnpj)}
                    </td>
                    <td className="whitespace-nowrap px-4 py-2.5">{c.telefone || "—"}</td>
                    <td className="whitespace-nowrap px-4 py-2.5">
                      {c.cidade ? `${c.cidade}${c.uf ? "/" + c.uf : ""}` : "—"}
                    </td>
                    <td className="whitespace-nowrap px-4 py-2.5 text-right font-mono tabular-nums">
                      {moeda(c.valorAReceber)}
                    </td>
                    <td className="whitespace-nowrap px-4 py-2.5">{dataBR(c.dataVencimento)}</td>
                    <td className="whitespace-nowrap px-4 py-2.5">
                      <Badge
                        texto={LABEL_SITUACAO[sit]}
                        tom={sit === "VENCIDO" ? "vencido" : proximo ? "proximo" : "ok"}
                      />
                    </td>
                    <td className="whitespace-nowrap px-4 py-2.5">
                      <Badge texto={inativo ? "Inativo" : "Ativo"} tom={inativo ? "inativo" : "ativo"} />
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      <div className="flex flex-wrap items-center justify-between gap-4 rounded-xl border border-borda bg-superficie px-4 py-3 shadow-sm">
        <div className="flex gap-2">
          <BotaoAcao
            icone={<IconeLapis className="h-4 w-4" />}
            texto="Editar"
            desabilitado={!selecionado || processando}
            onClick={onEditar}
          />
          <BotaoAcao
            icone={<IconeLixeira className="h-4 w-4 text-vencido" />}
            texto="Excluir"
            desabilitado={!selecionado || processando}
            onClick={onExcluir}
          />
          <BotaoAcao
            icone={<IconeReciclar className="h-4 w-4" />}
            texto="Ativar / Inativar"
            desabilitado={!selecionado || processando}
            onClick={onAlternarStatus}
            fantasma
          />
        </div>

        <div className="flex gap-7 text-right">
          <Metrica rotulo="Clientes" valor={String(resumo.quantidade)} />
          <Metrica rotulo="Total a receber" valor={moeda(resumo.total)} />
          <Metrica
            rotulo="Vencidos"
            valor={`${resumo.qtdVencidos}  (${moeda(resumo.totalVencido)})`}
            alerta={resumo.qtdVencidos > 0}
          />
        </div>
      </div>
    </div>
  );
}

function BotaoAcao({
  icone,
  texto,
  onClick,
  desabilitado,
  fantasma,
}: {
  icone: React.ReactNode;
  texto: string;
  onClick: () => void;
  desabilitado?: boolean;
  fantasma?: boolean;
}) {
  return (
    <button
      onClick={onClick}
      disabled={desabilitado}
      className={`flex items-center gap-2 rounded-lg px-3.5 py-2 text-sm font-semibold transition disabled:cursor-not-allowed disabled:opacity-40 ${
        fantasma ? "text-tinta-suave hover:bg-superficie-2" : "border border-borda-forte text-tinta hover:bg-superficie-2"
      }`}
    >
      {icone}
      {texto}
    </button>
  );
}

function Metrica({ rotulo, valor, alerta }: { rotulo: string; valor: string; alerta?: boolean }) {
  return (
    <div className="flex flex-col">
      <span className="text-[10px] font-semibold uppercase tracking-wide text-tinta-fraca">{rotulo}</span>
      <span className={`font-mono text-base font-semibold tabular-nums ${alerta ? "text-vencido" : "text-tinta"}`}>
        {valor}
      </span>
    </div>
  );
}
