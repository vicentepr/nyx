# Projeto Integrador - Front-end (Angular 19)

Este projeto implementa uma SPA completa para o Projeto Integrador, tomando como base as referências de **carro-front** e **carro-back** fornecidas em aula.

## Requisitos atendidos

- Angular 19 (estrutura preparada para a versão mais recente com componentes standalone).
- Estrutura organizada por `models`, `services` e `components`.
- Layout completo com tela de login independente, menu superior fixo e menu lateral.
- Integração com back-end via services (`Auth`, `Veículos` e `Fabricantes`).
- Consumo de todos os endpoints de CRUD + filtros personalizados.
- Tratamento de erros com `SweetAlert2` utilizando `error.error`.
- Relacionamentos entre entidades implementados com modal (`ManufacturerSelectorModalComponent`) utilizando property e event binding.
- Utilização do framework gráfico **MDB** e personalização de identidade visual.
- Configuração pronta para uso de `HttpInterceptor`, guardas de rota e autenticação baseada em token JWT.

## Scripts principais

```bash
npm install
npm start
npm run build
```

> Observação: devido ao ambiente acadêmico, a instalação das dependências deve ser realizada com acesso à internet liberado para o registro npm.

## Estrutura de pastas

```
frontend/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   ├── login/
│   │   │   ├── dashboard/
│   │   │   ├── vehicle/
│   │   │   └── manufacturer/
│   │   ├── core/
│   │   ├── layout/
│   │   ├── models/
│   │   ├── services/
│   │   ├── guards/
│   │   └── interceptors/
│   └── environments/
└── package.json
```

## Integração com o back-end

Atualize o arquivo `src/environments/environment.ts` com a URL correta do back-end. Todos os serviços seguem o padrão `/api` previsto no Projeto Integrador e aguardam as rotas `auth`, `veiculos` e `fabricantes` já implementadas no back-end.

Para garantir que o tratamento de exceções do back esteja alinhado com a classe `GlobalExceptionHandler`, certifique-se de retornar mensagens amigáveis em `error.error` para que o front consiga exibí-las nos alertas.

## Autenticação

O front espera que o endpoint `/auth/login` retorne um objeto contendo `token`, `name` e `roles`. O token é armazenado em `localStorage` e reaplicado em cada requisição via `auth.interceptor`.

## Modal de relacionamento

O componente `ManufacturerSelectorModalComponent` ilustra como implementar relacionamentos utilizando modais, `@Input` e `@Output`. Ao selecionar um fabricante, o modal emite o evento `selected`, que é tratado no formulário de veículos e vinculado ao registro antes do envio ao back-end.
