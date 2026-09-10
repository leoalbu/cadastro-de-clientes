/** Ícones de linha (stroke) simples, no mesmo estilo visual do app Java/web. */

type Props = { className?: string };
const base = {
  fill: "none",
  stroke: "currentColor",
  strokeWidth: 1.8,
  strokeLinecap: "round" as const,
  strokeLinejoin: "round" as const,
  viewBox: "0 0 24 24",
};

export function IconeMais({ className }: Props) {
  return (
    <svg className={className} {...base}>
      <path d="M12 5v14M5 12h14" />
    </svg>
  );
}

export function IconeLapis({ className }: Props) {
  return (
    <svg className={className} {...base}>
      <path d="M4 20h4L18.5 9.5a2.1 2.1 0 0 0-3-3L5 17v3Z" />
      <path d="m14 6 3 3" />
    </svg>
  );
}

export function IconeLixeira({ className }: Props) {
  return (
    <svg className={className} {...base}>
      <path d="M4 7h16M9 7V4h6v3M6 7l1 13h10l1-13" />
      <path d="M10 11v6M14 11v6" />
    </svg>
  );
}

export function IconeReciclar({ className }: Props) {
  return (
    <svg className={className} {...base}>
      <path d="M4 12a8 8 0 0 1 14-5.2M20 12a8 8 0 0 1-14 5.2" />
      <path d="M17 3v4h-4M7 21v-4h4" />
    </svg>
  );
}

export function IconeLupa({ className }: Props) {
  return (
    <svg className={className} {...base}>
      <circle cx="11" cy="11" r="7" />
      <path d="m21 21-4.3-4.3" />
    </svg>
  );
}

export function IconeFunil({ className }: Props) {
  return (
    <svg className={className} {...base}>
      <path d="M4 5h16l-6 7.5V19l-4 2v-8.5Z" />
    </svg>
  );
}

export function IconeBanco({ className }: Props) {
  return (
    <svg className={className} {...base}>
      <ellipse cx="12" cy="6" rx="8" ry="3" />
      <path d="M4 6v12c0 1.7 3.6 3 8 3s8-1.3 8-3V6" />
      <path d="M4 12c0 1.7 3.6 3 8 3s8-1.3 8-3" />
    </svg>
  );
}

export function IconeX({ className }: Props) {
  return (
    <svg className={className} {...base}>
      <path d="M6 6l12 12M18 6 6 18" />
    </svg>
  );
}

export function IconeAlerta({ className }: Props) {
  return (
    <svg className={className} {...base}>
      <path d="M12 9v4M12 17h.01" />
      <path d="M10.3 3.9 2.5 17.5a1.7 1.7 0 0 0 1.5 2.5h16a1.7 1.7 0 0 0 1.5-2.5L13.7 3.9a1.7 1.7 0 0 0-3.4 0Z" />
    </svg>
  );
}

export function IconeMensagem({ className }: Props) {
  return (
    <svg className={className} {...base}>
      <path d="M21 12a8 8 0 0 1-11.5 7.2L4 20l1-4.6A8 8 0 1 1 21 12Z" />
    </svg>
  );
}

export function IconeIA({ className }: Props) {
  return (
    <svg className={className} {...base}>
      <path d="M12 3l1.8 4.7L18.5 9.5 13.8 11.3 12 16l-1.8-4.7L5.5 9.5l4.7-1.8Z" />
      <path d="M18.5 15.5l.7 1.8 1.8.7-1.8.7-.7 1.8-.7-1.8-1.8-.7 1.8-.7Z" />
    </svg>
  );
}

export function IconeCopiar({ className }: Props) {
  return (
    <svg className={className} {...base}>
      <rect x="9" y="9" width="11" height="11" rx="2" />
      <path d="M5 15V5a2 2 0 0 1 2-2h8" />
    </svg>
  );
}

export function IconeCheck({ className }: Props) {
  return (
    <svg className={className} {...base}>
      <path d="M5 13l4 4L19 7" />
    </svg>
  );
}
