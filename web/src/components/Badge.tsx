const ESTILOS = {
  ok: "bg-ok-bg text-ok",
  proximo: "bg-proximo-bg text-proximo",
  vencido: "bg-vencido-bg text-vencido",
  ativo: "bg-acento-suave text-acento-hover",
  inativo: "bg-inativo-bg text-inativo",
} as const;

export function Badge({
  texto,
  tom,
}: {
  texto: string;
  tom: keyof typeof ESTILOS;
}) {
  return (
    <span
      className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-semibold ${ESTILOS[tom]}`}
    >
      {texto}
    </span>
  );
}
