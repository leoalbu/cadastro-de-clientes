"use client";

import { useState } from "react";
import { salvarClienteAction } from "@/app/actions";
import { Cliente, StatusCliente } from "@/lib/tipos";
import { parseValor } from "@/lib/formato";
import { formatarDoc } from "@/lib/validacao";
import { IconeLapis, IconeMais, IconeX } from "./Icones";

export function ModalCliente({
  cliente,
  aoFechar,
  aoSalvo,
}: {
  cliente: Cliente | null;
  aoFechar: () => void;
  aoSalvo: () => void;
}) {
  const novo = cliente === null;
  const [nome, setNome] = useState(cliente?.nome ?? "");
  const [doc, setDoc] = useState(cliente ? formatarDoc(cliente.cpfCnpj) : "");
  const [email, setEmail] = useState(cliente?.email ?? "");
  const [telefone, setTelefone] = useState(cliente?.telefone ?? "");
  const [cidade, setCidade] = useState(cliente?.cidade ?? "");
  const [uf, setUf] = useState(cliente?.uf ?? "");
  const [status, setStatus] = useState<StatusCliente>(cliente?.status ?? "ATIVO");
  const [valor, setValor] = useState(
    cliente && cliente.valorAReceber > 0 ? String(cliente.valorAReceber).replace(".", ",") : ""
  );
  const [vencimento, setVencimento] = useState(cliente?.dataVencimento ?? "");
  const [observacoes, setObservacoes] = useState(cliente?.observacoes ?? "");

  const [erros, setErros] = useState<string[]>([]);
  const [salvando, setSalvando] = useState(false);

  async function salvar(e: React.FormEvent) {
    e.preventDefault();
    setSalvando(true);
    setErros([]);

    const valorNumerico = parseValor(valor);
    const resultado = await salvarClienteAction({
      id: cliente?.id,
      nome,
      cpfCnpj: doc,
      email,
      telefone,
      cidade,
      uf,
      status,
      observacoes,
      valorAReceber: valorNumerico,
      dataVencimento: vencimento || null,
    });

    setSalvando(false);
    if (!resultado.ok) {
      setErros(resultado.erros ?? ["Não foi possível salvar."]);
      return;
    }
    aoSalvo();
  }

  return (
    <div
      className="fixed inset-0 z-40 flex items-start justify-center overflow-y-auto bg-tinta/50 p-6 sm:p-10"
      onMouseDown={(e) => e.target === e.currentTarget && aoFechar()}
    >
      <div className="w-full max-w-xl rounded-xl bg-superficie shadow-xl">
        <div className="flex items-center gap-3 border-b border-borda px-6 py-4">
          {novo ? (
            <IconeMais className="h-5 w-5 text-acento" />
          ) : (
            <IconeLapis className="h-5 w-5 text-acento" />
          )}
          <h2 className="text-lg font-semibold text-tinta">
            {novo ? "Novo cliente" : "Editar cliente"}
          </h2>
        </div>

        <form onSubmit={salvar} className="grid grid-cols-2 gap-4 px-6 py-5">
          {erros.length > 0 && (
            <div className="col-span-2 rounded-lg bg-vencido-bg px-4 py-3 text-sm text-vencido">
              <strong>Corrija os campos:</strong>
              <ul className="ml-4 list-disc">
                {erros.map((e) => (
                  <li key={e}>{e}</li>
                ))}
              </ul>
            </div>
          )}

          <Campo rotulo="Nome / Razão social" obrigatorio className="col-span-2">
            <input value={nome} onChange={(e) => setNome(e.target.value)} className={estiloInput} autoFocus />
          </Campo>

          <Campo rotulo="CPF ou CNPJ" obrigatorio>
            <input value={doc} onChange={(e) => setDoc(e.target.value)} className={estiloInput} />
          </Campo>
          <Campo rotulo="Telefone">
            <input value={telefone} onChange={(e) => setTelefone(e.target.value)} className={estiloInput} />
          </Campo>

          <Campo rotulo="E-mail">
            <input value={email} onChange={(e) => setEmail(e.target.value)} className={estiloInput} />
          </Campo>
          <Campo rotulo="Status">
            <select
              value={status}
              onChange={(e) => setStatus(e.target.value as StatusCliente)}
              className={estiloInput}
            >
              <option value="ATIVO">Ativo</option>
              <option value="INATIVO">Inativo</option>
            </select>
          </Campo>

          <Campo rotulo="Cidade">
            <input value={cidade} onChange={(e) => setCidade(e.target.value)} className={estiloInput} />
          </Campo>
          <Campo rotulo="UF">
            <input
              value={uf}
              maxLength={2}
              onChange={(e) => setUf(e.target.value.toUpperCase())}
              className={estiloInput}
            />
          </Campo>

          <Campo rotulo="Valor a receber (R$)">
            <input
              value={valor}
              onChange={(e) => setValor(e.target.value)}
              placeholder="0,00"
              className={estiloInput}
            />
          </Campo>
          <Campo rotulo="Data de vencimento">
            <input
              type="date"
              value={vencimento ?? ""}
              onChange={(e) => setVencimento(e.target.value)}
              className={estiloInput}
            />
            <span className="mt-1 text-xs text-tinta-fraca">Obrigatória quando há valor a receber.</span>
          </Campo>

          <Campo rotulo="Observações" className="col-span-2">
            <textarea
              value={observacoes}
              onChange={(e) => setObservacoes(e.target.value)}
              rows={2}
              className={estiloInput}
            />
          </Campo>

          <div className="col-span-2 mt-2 flex justify-end gap-2 border-t border-borda pt-4">
            <button
              type="button"
              onClick={aoFechar}
              className="flex items-center gap-1.5 rounded-lg px-4 py-2 text-sm font-semibold text-tinta-suave hover:bg-superficie-2"
            >
              <IconeX className="h-3.5 w-3.5" />
              Cancelar
            </button>
            <button
              type="submit"
              disabled={salvando}
              className="rounded-lg bg-acento px-5 py-2 text-sm font-semibold text-white hover:bg-acento-hover disabled:opacity-60"
            >
              {salvando ? "Salvando…" : "Salvar cliente"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

const estiloInput =
  "w-full rounded-lg border border-borda-forte px-3 py-2 text-sm outline-none focus:border-acento focus:ring-1 focus:ring-acento";

function Campo({
  rotulo,
  obrigatorio,
  className,
  children,
}: {
  rotulo: string;
  obrigatorio?: boolean;
  className?: string;
  children: React.ReactNode;
}) {
  return (
    <label className={`flex flex-col gap-1 text-sm ${className ?? ""}`}>
      <span className="text-tinta-suave">
        {rotulo}
        {obrigatorio && <span className="text-vencido"> *</span>}
      </span>
      {children}
    </label>
  );
}
