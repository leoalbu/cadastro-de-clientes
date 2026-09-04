import { listarClientes } from "@/lib/clientes";
import { FiltroClientes, SituacaoFinanceira, StatusCliente } from "@/lib/tipos";
import { PaginaCliente } from "@/components/PaginaCliente";

export const dynamic = "force-dynamic";

function paraTexto(v: string | string[] | undefined): string | undefined {
  return typeof v === "string" && v.length > 0 ? v : undefined;
}

export default async function Home({
  searchParams,
}: {
  searchParams: { [chave: string]: string | string[] | undefined };
}) {
  const filtro: FiltroClientes = {
    texto: paraTexto(searchParams.q),
    status: paraTexto(searchParams.status) as StatusCliente | undefined,
    situacao: paraTexto(searchParams.situacao) as SituacaoFinanceira | undefined,
    venceEmDias: paraTexto(searchParams.dias) ? Number(searchParams.dias) : undefined,
  };

  let clientes: Awaited<ReturnType<typeof listarClientes>> = [];
  let erro: string | null = null;
  try {
    clientes = await listarClientes(filtro);
  } catch (e) {
    erro = (e as Error).message;
  }

  return (
    <main className="min-h-screen bg-fundo">
      <PaginaCliente clientes={clientes} erro={erro} />
    </main>
  );
}
