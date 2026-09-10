"use client";

import { useState } from "react";
import { gerarCobrancaAction } from "@/app/actions";
import { Cliente } from "@/lib/tipos";
import { CanalCobranca, TomCobranca } from "@/lib/cobranca";
import { moeda } from "@/lib/formato";
import { situacaoFinanceira } from "@/lib/situacao";
import { IconeCheck, IconeCopiar, IconeIA, IconeMensagem, IconeX } from "./Icones";
import { Badge } from "./Badge";

const TONS: { valor: TomCobranca; rotulo: string }[] = [
  { valor: "amigavel", rotulo: "Amigável" },
  { valor: "neutro", rotulo: "Neutro" },
  { valor: "firme", rotulo: "Firme" },
];

const CANAIS: { valor: CanalCobranca; rotulo: string }[] = [
  { valor: "whatsapp", rotulo: "WhatsApp" },
  { valor: "email", rotulo: "E-mail" },
];

/** DDI + DDD + número, só dígitos. Prepara para o wa.me. */
function paraWhatsApp(telefone: string): string {
  const d = telefone.replace(/\D/g, "");
  if (d.length === 10 || d.length === 11) return "55" + d; // sem código do país
  return d; // já tem DDI ou formato incomum
}

/** Separa "Assunto: ..." da primeira linha, se existir. */
function separarAssunto(texto: string, nome: string): { assunto: string; corpo: string } {
  const m = texto.match(/^\s*assunto:\s*(.+?)\s*\r?\n+([\s\S]*)$/i);
  if (m) return { assunto: m[1].trim(), corpo: m[2].trim() };
  return { assunto: `Cobrança - ${nome}`, corpo: texto.trim() };
}

function abrirLink(url: string) {
  const a = document.createElement("a");
  a.href = url;
  a.target = "_blank";
  a.rel = "noopener noreferrer";
  document.body.appendChild(a);
  a.click();
  a.remove();
}

export function ModalCobranca({ cliente, aoFechar }: { cliente: Cliente; aoFechar: () => void }) {
  const [tom, setTom] = useState<TomCobranca>("neutro");
  const [canal, setCanal] = useState<CanalCobranca>("whatsapp");
  const [texto, setTexto] = useState("");
  const [canalGerado, setCanalGerado] = useState<CanalCobranca | null>(null);
  const [destino, setDestino] = useState("");
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);
  const [copiado, setCopiado] = useState(false);

  const sit = situacaoFinanceira(cliente);

  async function gerar() {
    setCarregando(true);
    setErro(null);
    setCopiado(false);
    const r = await gerarCobrancaAction(cliente.id, tom, canal);
    setCarregando(false);
    if (!r.ok) {
      setErro(r.erro ?? "Não foi possível gerar a cobrança.");
      return;
    }
    setTexto(r.texto ?? "");
    setCanalGerado(canal);
    setDestino(canal === "whatsapp" ? cliente.telefone ?? "" : cliente.email ?? "");
  }

  async function copiar() {
    try {
      await navigator.clipboard.writeText(texto);
      setCopiado(true);
      setTimeout(() => setCopiado(false), 2000);
    } catch {
      setErro("Não foi possível copiar. Selecione o texto e copie manualmente.");
    }
  }

  function enviar() {
    if (!destino.trim()) {
      setErro(
        canal === "whatsapp"
          ? "Informe o telefone com DDD para enviar no WhatsApp."
          : "Informe o e-mail do cliente para enviar."
      );
      return;
    }
    setErro(null);
    if (canal === "whatsapp") {
      const numero = paraWhatsApp(destino);
      abrirLink(`https://wa.me/${numero}?text=${encodeURIComponent(texto)}`);
    } else {
      const { assunto, corpo } = separarAssunto(texto, cliente.nome);
      abrirLink(
        `mailto:${destino.trim()}?subject=${encodeURIComponent(assunto)}&body=${encodeURIComponent(
          corpo
        )}`
      );
    }
  }

  const podeEnviar = texto.length > 0;
  const canalMudou = canalGerado !== null && canalGerado !== canal;

  return (
    <div
      className="fixed inset-0 z-40 flex items-start justify-center overflow-y-auto bg-tinta/50 p-6 sm:p-10"
      onMouseDown={(e) => e.target === e.currentTarget && aoFechar()}
    >
      <div className="w-full max-w-xl rounded-xl bg-superficie shadow-xl">
        <div className="flex items-center gap-3 border-b border-borda px-6 py-4">
          <IconeIA className="h-5 w-5 text-acento" />
          <div>
            <h2 className="text-lg font-semibold text-tinta">Assistente de cobrança</h2>
            <p className="text-xs text-tinta-suave">
              {cliente.nome} · {moeda(cliente.valorAReceber)}{" "}
              <span className="align-middle">
                <Badge
                  texto={
                    { EM_DIA: "Em dia", A_VENCER: "A vencer", VENCE_HOJE: "Vence hoje", VENCIDO: "Vencido" }[
                      sit
                    ]
                  }
                  tom={sit === "VENCIDO" ? "vencido" : sit === "EM_DIA" || sit === "A_VENCER" ? "ok" : "proximo"}
                />
              </span>
            </p>
          </div>
        </div>

        <div className="space-y-4 px-6 py-5">
          <div className="flex flex-wrap gap-4">
            <Grupo rotulo="Tom">
              {TONS.map((t) => (
                <Opcao key={t.valor} ativo={tom === t.valor} onClick={() => setTom(t.valor)}>
                  {t.rotulo}
                </Opcao>
              ))}
            </Grupo>
            <Grupo rotulo="Canal">
              {CANAIS.map((c) => (
                <Opcao key={c.valor} ativo={canal === c.valor} onClick={() => setCanal(c.valor)}>
                  {c.rotulo}
                </Opcao>
              ))}
            </Grupo>
          </div>

          {erro && (
            <div className="rounded-lg bg-vencido-bg px-4 py-3 text-sm text-vencido">{erro}</div>
          )}

          <textarea
            value={texto}
            onChange={(e) => setTexto(e.target.value)}
            rows={8}
            placeholder={
              carregando ? "Gerando…" : 'Clique em "Gerar mensagem" para criar o texto de cobrança.'
            }
            className="w-full rounded-lg border border-borda-forte px-3 py-2 text-sm leading-relaxed outline-none focus:border-acento focus:ring-1 focus:ring-acento"
          />

          {podeEnviar && (
            <div className="space-y-2">
              <label className="flex flex-col gap-1 text-sm">
                <span className="text-tinta-suave">
                  {canal === "whatsapp" ? "Telefone (com DDD)" : "E-mail do cliente"}
                </span>
                <input
                  value={destino}
                  onChange={(e) => setDestino(e.target.value)}
                  placeholder={canal === "whatsapp" ? "(11) 98888-1111" : "cliente@email.com"}
                  className="w-full rounded-lg border border-borda-forte px-3 py-2 text-sm outline-none focus:border-acento focus:ring-1 focus:ring-acento"
                />
              </label>
              {canalMudou && (
                <p className="text-xs text-proximo">
                  O texto foi gerado para {canalGerado === "whatsapp" ? "WhatsApp" : "e-mail"}. Clique em
                  &ldquo;Gerar novamente&rdquo; para o formato de {canal === "whatsapp" ? "WhatsApp" : "e-mail"}.
                </p>
              )}
              <p className="text-xs text-tinta-fraca">
                Revise antes de enviar — a IA pode errar. O botão abre o {canal === "whatsapp" ? "WhatsApp" : "seu app de e-mail"} com a mensagem já preenchida.
              </p>
            </div>
          )}
        </div>

        <div className="flex items-center justify-between gap-2 border-t border-borda px-6 py-4">
          <button
            type="button"
            onClick={aoFechar}
            className="flex items-center gap-1.5 rounded-lg px-4 py-2 text-sm font-semibold text-tinta-suave hover:bg-superficie-2"
          >
            <IconeX className="h-3.5 w-3.5" />
            Fechar
          </button>
          <div className="flex flex-wrap gap-2">
            {podeEnviar && (
              <>
                <button
                  type="button"
                  onClick={copiar}
                  className="flex items-center gap-1.5 rounded-lg border border-borda-forte px-3 py-2 text-sm font-semibold text-tinta hover:bg-superficie-2"
                >
                  {copiado ? <IconeCheck className="h-4 w-4 text-acento" /> : <IconeCopiar className="h-4 w-4" />}
                  {copiado ? "Copiado" : "Copiar"}
                </button>
                <button
                  type="button"
                  onClick={enviar}
                  className="flex items-center gap-1.5 rounded-lg bg-acento px-4 py-2 text-sm font-semibold text-white hover:bg-acento-hover"
                >
                  <IconeMensagem className="h-4 w-4" />
                  {canal === "whatsapp" ? "Enviar no WhatsApp" : "Enviar por e-mail"}
                </button>
              </>
            )}
            <button
              type="button"
              onClick={gerar}
              disabled={carregando}
              className="flex items-center gap-1.5 rounded-lg border border-acento px-4 py-2 text-sm font-semibold text-acento hover:bg-acento-suave disabled:opacity-60"
            >
              <IconeIA className="h-4 w-4" />
              {carregando ? "Gerando…" : texto ? "Gerar novamente" : "Gerar mensagem"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

function Grupo({ rotulo, children }: { rotulo: string; children: React.ReactNode }) {
  return (
    <div className="flex flex-col gap-1.5">
      <span className="text-[10px] font-semibold uppercase tracking-wide text-tinta-fraca">
        {rotulo}
      </span>
      <div className="flex gap-1.5">{children}</div>
    </div>
  );
}

function Opcao({
  ativo,
  onClick,
  children,
}: {
  ativo: boolean;
  onClick: () => void;
  children: React.ReactNode;
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`rounded-lg px-3 py-1.5 text-sm font-semibold transition ${
        ativo
          ? "bg-acento text-white"
          : "border border-borda-forte text-tinta-suave hover:bg-superficie-2"
      }`}
    >
      {children}
    </button>
  );
}
