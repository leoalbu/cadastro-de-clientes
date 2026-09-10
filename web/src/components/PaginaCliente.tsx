"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Cliente } from "@/lib/tipos";
import { alternarStatusAction, excluirClienteAction } from "@/app/actions";
import { Cabecalho } from "./Cabecalho";
import { Filtros } from "./Filtros";
import { PainelClientes } from "./PainelClientes";
import { ModalCliente } from "./ModalCliente";
import { ModalCobranca } from "./ModalCobranca";
import { ErroConexao } from "./ErroConexao";

export function PaginaCliente({ clientes, erro }: { clientes: Cliente[]; erro: string | null }) {
  const router = useRouter();
  const [selecionadoId, setSelecionadoId] = useState<number | null>(null);
  const [modal, setModal] = useState<"fechado" | "novo" | "editar">("fechado");
  const [cobrancaAberta, setCobrancaAberta] = useState(false);
  const [processando, setProcessando] = useState(false);

  const selecionado = clientes.find((c) => c.id === selecionadoId) ?? null;

  function atualizar() {
    router.refresh();
  }

  async function excluir() {
    if (!selecionado) return;
    if (!confirm(`Excluir definitivamente o cliente "${selecionado.nome}"?`)) return;
    setProcessando(true);
    const r = await excluirClienteAction(selecionado.id);
    setProcessando(false);
    if (!r.ok) {
      alert(r.erros?.join("\n") ?? "Não foi possível excluir.");
      return;
    }
    setSelecionadoId(null);
    atualizar();
  }

  async function alternarStatus() {
    if (!selecionado) return;
    setProcessando(true);
    const r = await alternarStatusAction(selecionado.id, selecionado.status);
    setProcessando(false);
    if (!r.ok) {
      alert(r.erros?.join("\n") ?? "Não foi possível alterar o status.");
      return;
    }
    atualizar();
  }

  return (
    <div className="mx-auto flex max-w-6xl flex-col gap-5 px-6 py-8">
      <Cabecalho
        fonteDados={erro ? "Indisponível" : "Supabase"}
        aoNovo={() => {
          setSelecionadoId(null);
          setModal("novo");
        }}
      />
      <Filtros />

      {erro ? (
        <ErroConexao mensagem={erro} />
      ) : (
        <PainelClientes
          clientes={clientes}
          selecionadoId={selecionadoId}
          processando={processando}
          onSelecionar={setSelecionadoId}
          onAbrirEdicao={() => setModal("editar")}
          onEditar={() => setModal("editar")}
          onExcluir={excluir}
          onAlternarStatus={alternarStatus}
          onCobranca={() => setCobrancaAberta(true)}
        />
      )}

      {modal !== "fechado" && (
        <ModalCliente
          cliente={modal === "editar" ? selecionado : null}
          aoFechar={() => setModal("fechado")}
          aoSalvo={() => {
            setModal("fechado");
            atualizar();
          }}
        />
      )}

      {cobrancaAberta && selecionado && (
        <ModalCobranca cliente={selecionado} aoFechar={() => setCobrancaAberta(false)} />
      )}
    </div>
  );
}
