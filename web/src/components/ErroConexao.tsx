import { IconeAlerta } from "./Icones";

export function ErroConexao({ mensagem }: { mensagem: string }) {
  return (
    <div className="flex items-start gap-3 rounded-xl border border-vencido/30 bg-vencido-bg px-5 py-4 text-vencido">
      <IconeAlerta className="mt-0.5 h-5 w-5 shrink-0" />
      <div className="text-sm">
        <p className="font-semibold">Não foi possível conectar ao Supabase.</p>
        <p className="mt-1 whitespace-pre-wrap opacity-90">{mensagem}</p>
        <p className="mt-2 opacity-90">
          Confira <code className="rounded bg-white/50 px-1">web/.env.local</code>, se a tabela
          <code className="rounded bg-white/50 px-1">clientes</code> existe (rode{" "}
          <code className="rounded bg-white/50 px-1">db/schema.sql</code>) e a policy de RLS.
        </p>
      </div>
    </div>
  );
}
