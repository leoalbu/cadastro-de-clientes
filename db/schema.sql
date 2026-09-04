-- ============================================================
--  Cadastro de Clientes - schema para o Supabase (PostgreSQL)
--  Rode no Supabase Dashboard > SQL Editor > New query.
--  Modelo atual do app: UM saldo a receber por cliente.
-- ============================================================

create table if not exists public.clientes (
    id               bigint generated always as identity primary key,
    nome             text          not null,
    cpf_cnpj         text          not null unique,
    email            text,
    telefone         text,
    cidade           text,
    uf               text,
    status           text          not null default 'ATIVO'
                     check (status in ('ATIVO', 'INATIVO')),
    observacoes      text,
    valor_a_receber  numeric(12,2) not null default 0
                     check (valor_a_receber >= 0),
    data_vencimento  date,
    data_cadastro    date          not null default current_date,
    created_at       timestamptz   not null default now()
);

-- Indices que ajudam os filtros do app (nome, status, vencimento)
create index if not exists idx_clientes_nome       on public.clientes (lower(nome));
create index if not exists idx_clientes_status     on public.clientes (status);
create index if not exists idx_clientes_vencimento on public.clientes (data_vencimento);

-- ------------------------------------------------------------
--  Row Level Security (RLS)
-- ------------------------------------------------------------
alter table public.clientes enable row level security;

-- ATENCAO: politica permissiva, apenas para ESTUDO.
-- Com ela, qualquer pessoa que tenha a anon key pode ler e gravar.
-- Para uso real, remova esta policy e crie regras baseadas em auth.uid().
drop policy if exists "estudo_acesso_total" on public.clientes;
create policy "estudo_acesso_total"
    on public.clientes
    for all
    to anon, authenticated
    using (true)
    with check (true);

-- ------------------------------------------------------------
--  Dados de exemplo (opcional) - descomente para inserir
-- ------------------------------------------------------------
-- insert into public.clientes (nome, cpf_cnpj, email, telefone, cidade, uf, valor_a_receber, data_vencimento, status) values
--   ('Maria Oliveira Souza',   '52998224725',    'maria.souza@email.com',        '(11) 98888-1111', 'Sao Paulo',       'SP', 1250.00, current_date - 10, 'ATIVO'),
--   ('Joao Pedro Almeida',     '39053344705',    'joao.almeida@email.com',       '(21) 97777-2222', 'Rio de Janeiro',  'RJ',  480.50, current_date + 3,  'ATIVO'),
--   ('Comercio Silva LTDA',    '11444777000161', 'contato@comerciosilva.com.br', '(31) 3333-4444',  'Belo Horizonte',  'MG', 7200.00, current_date + 25, 'ATIVO');
