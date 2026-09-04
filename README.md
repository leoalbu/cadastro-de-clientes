# Cadastro de Clientes

Aplicativo desktop (Java + Swing) para cadastro de clientes com controle de
valores a receber. Feito para estudo: sem dependências externas, sem build tool,
dados apenas em memória (somem ao fechar o programa).

## Requisitos

- JDK 19 ou superior (`java -version` para conferir). Testado com JDK 21.

## Como compilar e executar

No Windows, a partir da pasta do projeto:

```bat
compilar.bat
executar.bat
```

Manualmente (qualquer sistema operacional):

```bash
javac -encoding UTF-8 -d bin $(find src -name "*.java")
java -cp bin com.leandro.cadastro.Main
```

O programa abre com 8 clientes de exemplo (alguns em dia, alguns a vencer,
alguns vencidos, dois inativos).

## Funcionalidades

- **CRUD** de clientes: Novo, Editar (ou duplo clique na linha), Excluir (com
  confirmação) e Ativar/Inativar (mantém o registro, só muda o status).
- **Valores a receber**: cada cliente tem um valor e uma data de vencimento. A
  situação (Em dia / A vencer / Vence hoje / Vencido) é calculada na hora.
  Linhas vencidas aparecem em vermelho; vencendo em até 7 dias, em amarelo.
- **Filtros** (barra superior): por nome ou CPF/CNPJ, por status, por situação
  financeira e por "vence em até N dias". Botão **Limpar** zera tudo.
- **Ordenação**: clique no cabeçalho de qualquer coluna.
- **Resumo** no rodapé: quantidade de clientes, total a receber e total vencido
  da lista atualmente filtrada.
- **Validações** ao salvar: nome obrigatório, CPF/CNPJ obrigatório, válido
  (dígitos verificadores) e único, e-mail em formato correto, valor não
  negativo, vencimento obrigatório quando há valor a receber.

## Banco de dados (Supabase, via API REST)

O app funciona em memória por padrão. Se o arquivo `.env` estiver configurado
com um projeto Supabase, ele passa a ler e gravar na tabela `clientes` pela
API REST (PostgREST) — sem driver JDBC, só `java.net.http.HttpClient`.

### Passos

1. Crie um projeto em [supabase.com](https://supabase.com).
2. No painel: **SQL Editor › New query**, cole o conteúdo de
   [`db/schema.sql`](db/schema.sql) e execute. Isso cria a tabela `clientes`,
   os índices e uma policy de RLS permissiva (**apenas para estudo**).
3. Em **Project Settings › API**, copie **Project URL** e a chave **anon**.
4. Copie `.env.example` para `.env` e preencha:

   ```
   SUPABASE_URL=https://SEU-PROJETO.supabase.co
   SUPABASE_REST_URL=https://SEU-PROJETO.supabase.co/rest/v1
   SUPABASE_ANON_KEY=sua-anon-key
   ```

5. Teste sem abrir a tela:

   ```
   java -cp bin com.leandro.cadastro.TestarConexao
   ```

6. Rode o app normalmente (`executar.bat`). O console mostra
   `[info] Conectado ao Supabase: ...`. Se a conexão falhar, o app avisa e
   abre em memória.

> O `.env` está no `.gitignore` e não deve ser versionado. A chave `anon` é
> pública por design (vai embutida em apps cliente) e é protegida pelo RLS —
> por isso a policy do `schema.sql` deve ser trocada por regras reais antes de
> qualquer uso sério.

## Estrutura do projeto

```
src/com/leandro/cadastro/
  Main.java              ponto de entrada; escolhe memória ou Supabase pelo .env
  TestarConexao.java     checa a conexão com o Supabase pela linha de comando
  model/                 Cliente, Endereco, StatusCliente, SituacaoFinanceira
  repository/            ClienteRepository (interface),
                         ClienteRepositoryMemoria, ClienteRepositorySupabase
  service/               ClienteService (regras/validação), FiltroClientes, ResumoFinanceiro
  exception/             ValidacaoException
  util/                  ValidadorCpfCnpj, ValidadorEmail, Formatos, Env, Json
  ui/                    JanelaPrincipal, ClienteTableModel, FormularioClienteDialog,
                         RenderizadorTabela, RenderizadorCabecalho
  ui/tema/               Cores, Fontes, Icones (ícones vetoriais em Java2D)
  ui/comp/               BotaoModerno, PainelCard, CampoBusca
db/schema.sql            script de criação da tabela no Supabase
.env.example             modelo de configuração (copie para .env)
web/                      app Next.js conectado ao Supabase, ver web/README.md
web-simulacao-estatica/  1ª versão web (só localStorage, sem backend)
```

A interface conversa apenas com `ClienteService`, que por sua vez usa a
interface `ClienteRepository`. Trocar de fonte de dados é só instanciar outra
implementação em `Main` — a UI e as regras não mudam.

## Outras versões do cadastro

Além do app desktop (Java/Swing), o mesmo banco Supabase também é acessado por:

- **[web/](web/README.md)** — app em Next.js (App Router + Server Actions),
  pronto para publicar no Railway. CRUD completo, mesmas regras de validação
  e cálculo de situação financeira do app Java.
- **[web-simulacao-estatica/](web-simulacao-estatica/index.html)** — a
  primeira versão, um único HTML com `localStorage` (sem backend), útil como
  protótipo rápido.

## Ideias de evolução

- Vários títulos por cliente, com parcelamento.
- Exportar a lista filtrada para CSV.
- Testes automatizados com JUnit (Java) e Vitest/Playwright (Next.js).
- Autenticação de usuários (Supabase Auth) e policies de RLS por usuário.
