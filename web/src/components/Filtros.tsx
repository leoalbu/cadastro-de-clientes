"use client";

import { useRouter, useSearchParams } from "next/navigation";
import { useState, useTransition } from "react";
import { IconeFunil, IconeLupa, IconeX } from "./Icones";

export function Filtros() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [, iniciarTransicao] = useTransition();

  const [texto, setTexto] = useState(searchParams.get("q") ?? "");
  const [status, setStatus] = useState(searchParams.get("status") ?? "");
  const [situacao, setSituacao] = useState(searchParams.get("situacao") ?? "");
  const [usarDias, setUsarDias] = useState(searchParams.get("dias") !== null);
  const [dias, setDias] = useState(searchParams.get("dias") ?? "7");

  function aplicar(overrides: Record<string, string | null> = {}) {
    const params = new URLSearchParams();
    const valores: Record<string, string | null> = {
      q: texto || null,
      status: status || null,
      situacao: situacao || null,
      dias: usarDias ? dias : null,
      ...overrides,
    };
    for (const [chave, valor] of Object.entries(valores)) {
      if (valor) params.set(chave, valor);
    }
    iniciarTransicao(() => {
      router.push(params.size ? `/?${params.toString()}` : "/");
    });
  }

  function limpar() {
    setTexto("");
    setStatus("");
    setSituacao("");
    setUsarDias(false);
    setDias("7");
    iniciarTransicao(() => router.push("/"));
  }

  return (
    <div className="rounded-xl border border-borda bg-superficie p-4 shadow-sm">
      <div className="flex flex-wrap items-end gap-4">
        <Campo rotulo="Busca">
          <div className="relative">
            <IconeLupa className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-tinta-fraca" />
            <input
              value={texto}
              onChange={(e) => setTexto(e.target.value)}
              onKeyDown={(e) => (e.key === "Enter" || e.keyCode === 13) && aplicar()}
              placeholder="Nome ou CPF/CNPJ"
              className="w-56 rounded-lg border border-borda-forte py-2 pl-9 pr-3 text-sm outline-none focus:border-acento focus:ring-1 focus:ring-acento"
            />
          </div>
        </Campo>

        <Campo rotulo="Status">
          <select
            value={status}
            onChange={(e) => setStatus(e.target.value)}
            className="rounded-lg border border-borda-forte px-3 py-2 text-sm outline-none focus:border-acento"
          >
            <option value="">Todos</option>
            <option value="ATIVO">Ativo</option>
            <option value="INATIVO">Inativo</option>
          </select>
        </Campo>

        <Campo rotulo="Situação">
          <select
            value={situacao}
            onChange={(e) => setSituacao(e.target.value)}
            className="rounded-lg border border-borda-forte px-3 py-2 text-sm outline-none focus:border-acento"
          >
            <option value="">Todas</option>
            <option value="EM_DIA">Em dia</option>
            <option value="A_VENCER">A vencer</option>
            <option value="VENCE_HOJE">Vence hoje</option>
            <option value="VENCIDO">Vencido</option>
          </select>
        </Campo>

        <Campo rotulo="Vencimento próximo">
          <div className="flex items-center gap-2 text-sm text-tinta-suave">
            <label className="flex items-center gap-1.5">
              <input
                type="checkbox"
                checked={usarDias}
                onChange={(e) => setUsarDias(e.target.checked)}
                className="h-4 w-4 rounded border-borda-forte text-acento focus:ring-acento"
              />
              vence em até
            </label>
            <input
              type="number"
              min={0}
              max={365}
              value={dias}
              disabled={!usarDias}
              onChange={(e) => setDias(e.target.value)}
              className="w-16 rounded-lg border border-borda-forte px-2 py-2 text-sm outline-none focus:border-acento disabled:bg-superficie-2"
            />
            <span>dias</span>
          </div>
        </Campo>

        <div className="flex gap-2">
          <button
            onClick={() => aplicar()}
            className="flex items-center gap-2 rounded-lg border border-borda-forte bg-superficie px-4 py-2 text-sm font-semibold text-tinta transition hover:bg-superficie-2"
          >
            <IconeFunil className="h-4 w-4" />
            Filtrar
          </button>
          <button
            onClick={limpar}
            className="flex items-center gap-2 rounded-lg px-3 py-2 text-sm font-semibold text-tinta-suave transition hover:bg-superficie-2"
          >
            <IconeX className="h-3.5 w-3.5" />
            Limpar
          </button>
        </div>
      </div>
    </div>
  );
}

function Campo({ rotulo, children }: { rotulo: string; children: React.ReactNode }) {
  return (
    <div className="flex flex-col gap-1.5">
      <span className="text-[10px] font-semibold uppercase tracking-wide text-tinta-fraca">
        {rotulo}
      </span>
      {children}
    </div>
  );
}
