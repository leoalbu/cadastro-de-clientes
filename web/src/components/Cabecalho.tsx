import { IconeBanco, IconeMais } from "./Icones";

export function Cabecalho({
  fonteDados,
  aoNovo,
}: {
  fonteDados: "Supabase" | "Indisponível";
  aoNovo: () => void;
}) {
  const conectado = fonteDados === "Supabase";
  return (
    <header className="flex flex-wrap items-center justify-between gap-4">
      <div className="flex items-center gap-3">
        <IconeBanco className="h-7 w-7 text-acento" />
        <div>
          <h1 className="text-xl font-bold tracking-tight text-tinta sm:text-2xl">
            Cadastro de Clientes
          </h1>
          <p className="text-sm text-tinta-suave">Contas a receber, vencimentos e filtros</p>
        </div>
      </div>
      <div className="flex items-center gap-3">
        <span
          className={`rounded-full px-3 py-1.5 text-xs font-semibold ${
            conectado ? "bg-acento-suave text-acento-hover" : "bg-inativo-bg text-inativo"
          }`}
        >
          ●&nbsp; {fonteDados}
        </span>
        <button
          onClick={aoNovo}
          className="flex items-center gap-2 rounded-lg bg-acento px-4 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-acento-hover"
        >
          <IconeMais className="h-4 w-4" />
          Novo cliente
        </button>
      </div>
    </header>
  );
}
