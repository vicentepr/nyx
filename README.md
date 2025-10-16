# Nyx – Catálogo digital de saches de nicotina

Este repositório reúne um protótipo de experiência full-stack para a Nyx, marca fictícia de saches de
nicotina. O objetivo é apresentar um painel informativo com catálogo de produtos, narrativa da marca e um
canal de contato comercial, sustentado por uma API simples construída apenas com módulos nativos do Node.js.

## Estrutura

```
frontend/   # Landing page estática com HTML, CSS e JavaScript moderno
backend/    # Servidor HTTP com rotas REST para catálogo, história, insights e contato
```

## Pré-requisitos

- [Node.js](https://nodejs.org/) 18 ou superior (para executar a API)
- Qualquer servidor HTTP estático (ex.: `npx serve`, extensões do VSCode, ou o próprio backend expondo a
  pasta `frontend`)

## Como executar a API

```bash
node backend/server.js
```

Por padrão a API sobe na porta `3001`. Ajuste a variável `PORT` antes de executar se necessário.

### Endpoints disponíveis

- `GET /api/products` — Lista produtos, aceitando filtros opcionais `strength`, `flavor` e `maxNicotine`.
- `GET /api/products/:id` — Detalhes de um produto específico.
- `GET /api/story` — Retorna headline, missão e marcos da Nyx.
- `GET /api/insights` — Entrega dados agregados (estoque total, força média, destaque de vendas).
- `POST /api/contact` — Recebe mensagens de contato e retorna eco com timestamp.

## Servindo o front-end

Qualquer servidor estático pode ser usado. Exemplo rápido usando o utilitário `http-server` do Node (instale
com `npm install -g http-server` se não tiver uma alternativa):

```bash
cd frontend
http-server
```

Se rodar o front-end em origem diferente da API, ajuste o atributo `data-api-base-url` no `<body>` do
`frontend/index.html` ou defina `window.API_BASE_URL` antes de carregar `app.js`.

## Fluxo de desenvolvimento

1. Inicie a API com `node backend/server.js`.
2. Sirva a pasta `frontend/` em outra porta (por exemplo `127.0.0.1:8080`).
3. Acesse o site, filtre produtos, visualize história e teste o formulário de contato.

## Próximos passos sugeridos

- Persistir contatos em um banco de dados real (SQLite ou Postgres).
- Implementar autenticação para um painel administrativo.
- Adicionar testes automatizados (ex.: `supertest` para a API, `vitest` para o front-end).
- Integrar ferramentas de build (Vite / Next.js) para otimizações adicionais.
