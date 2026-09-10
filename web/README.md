# Cadastro de Clientes — Web (Next.js)

Versão web do cadastro, feita em **Next.js 13 (App Router)**, conectada ao
**Supabase** (mesmo banco usado pelo app Java) e pronta para publicar no
**Railway**.

Ao contrário da [simulação estática](../web-simulacao-estatica/index.html)
(que só usava `localStorage`), esta versão lê e grava direto no Postgres do
Supabase — é o mesmo dado que o app desktop mostra.

## Stack

- **Next.js 13.5** (App Router, Server Components, Server Actions) — pinado
  em 13 porque este PC tem Node 16.14; para rodar Next 14/15 seria preciso
  Node 18.18+. Em produção (Railway) isso não importa: pode-se usar Node mais
  novo lá sem mudar nada aqui.
- **TypeScript** + **Tailwind CSS** (paleta própria em `tailwind.config.ts`,
  a mesma identidade visual do app Java e da simulação web).
- **@supabase/supabase-js** — chamado só no servidor (Server Components e
  Server Actions em `src/app/actions.ts`), a chave nunca vai para o navegador.

## Rodar localmente

```bash
cd web
npm install
```

Copie `.env.example` para `.env.local` e preencha com os dados do seu projeto
Supabase (painel: **Project Settings › API**):

```
SUPABASE_URL=https://SEU-PROJETO.supabase.co
SUPABASE_ANON_KEY=sua-anon-key
SUPABASE_TABLE_CLIENTES=clientes
OPENROUTER_API_KEY=sua-chave-openrouter   # assistente de cobrança (IA)
OPENROUTER_MODEL=openai/gpt-4o-mini       # opcional
```

A `OPENROUTER_API_KEY` sai de https://openrouter.ai/keys. Sem ela, o app roda
normal — só o botão "Cobrança (IA)" fica indisponível.

> A tabela precisa existir — rode [`../db/schema.sql`](../db/schema.sql) no
> SQL Editor do Supabase antes (só uma vez; é o mesmo banco do app Java).

```bash
npm run dev
```

Abra http://localhost:3000.

## Estrutura

```
web/
  src/
    app/
      page.tsx        Server Component: busca clientes (com filtro da URL) e renderiza a página
      actions.ts       Server Actions: salvar, excluir, alternar status (validação igual ao Java)
      layout.tsx
      globals.css
    components/        Cabecalho, Filtros, PainelClientes, ModalCliente, ModalCobranca, Badge, Icones
    lib/
      supabase.ts      cliente Supabase (server-only)
      clientes.ts      acesso a dados: listar/buscar/salvar/excluir/alternar status
      ia.ts            chamada de LLM via OpenRouter (server-only)
      cobranca.ts      monta o prompt e gera o texto de cobrança
      tipos.ts         tipos TypeScript + mapeamento snake_case <-> camelCase
      validacao.ts     CPF/CNPJ e e-mail (mesma regra do Java)
      situacao.ts      cálculo de EM_DIA/A_VENCER/VENCE_HOJE/VENCIDO
      formato.ts       moeda (BRL) e datas
```

## Assistente de cobrança (IA)

Selecione um cliente com valor a receber e clique em **Cobrança (IA)**. A
`gerarCobrancaAction` (Server Action) monta um prompt com nome, valor e
situação do vencimento e chama a OpenRouter (`src/lib/ia.ts`), devolvendo um
texto pronto para WhatsApp ou e-mail, em 3 tons (amigável / neutro / firme). O
texto é editável e tem botão de copiar. A chave `OPENROUTER_API_KEY` só existe
no servidor — nunca vai para o navegador.

Filtros (busca, status, situação, "vence em até N dias") viram parâmetros na
URL (`/?q=...&status=...`) — o `page.tsx` os lê e busca no Supabase já
filtrado no servidor. Criar/editar/excluir/ativar-inativar são **Server
Actions** chamadas diretamente pelos componentes cliente; cada uma revalida a
página (`revalidatePath("/")`) e o cliente chama `router.refresh()`.

## Publicar no Railway

1. Suba o repositório para o GitHub (o Railway faz deploy a partir de um repo).
2. No Railway: **New Project › Deploy from GitHub repo**, escolha este
   repositório.
3. Como o projeto Next.js fica dentro de `web/` (não na raiz do repo), abra
   **Settings** do serviço e defina **Root Directory** como `web`.
4. Em **Variables**, adicione:
   - `SUPABASE_URL`
   - `SUPABASE_ANON_KEY`
   - `SUPABASE_TABLE_CLIENTES` = `clientes`
   - `OPENROUTER_API_KEY` (para o assistente de cobrança)
5. O Railway detecta o Next.js automaticamente (Nixpacks) e roda
   `npm run build` seguido de `npm run start`. Ele também define a variável
   `PORT` sozinho — o `next start` já respeita isso, nada a configurar.
6. Ao terminar o deploy, o Railway dá uma URL pública (`*.up.railway.app`).

## Notas

- A policy de RLS criada em `db/schema.sql` é **permissiva** (qualquer um com
  a anon key lê/grava) — combinada propositalmente para estudo. Antes de
  expor a URL publicamente para outras pessoas, revise essa policy.
- `npm run build` compila com o mesmo Next 13; se atualizar o Node localmente
  para 18+, pode subir para Next 14/15 depois (`npm install next@latest`).
