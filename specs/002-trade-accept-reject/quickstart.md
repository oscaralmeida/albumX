# Quickstart: Evolução 1 — Aceitar e Recusar Trocas

**Feature**: `002-trade-accept-reject`  
**Branch de implementação**: `002-trade-accept-reject`  
**Plano**: [plan.md](./plan.md) | **Contrato**: [contracts/openapi.yaml](./contracts/openapi.yaml)  
**Depende de**: MVP estável ([../001-sticker-trade-mvp/quickstart.md](../001-sticker-trade-mvp/quickstart.md))

## Pré-requisitos

- MVP entregue e funcional (usuários, coleção, repetidas, criar proposta).
- Docker Desktop **ou** JDK 21 + Maven + Node.js 20 (mesmo setup do MVP).

## Execução

```bash
# Na raiz do repositório, branch 002-trade-accept-reject
docker-compose up --build
```

| Serviço | URL |
|---------|-----|
| **Frontend** | http://localhost:3000 |
| **API** | http://localhost:8080 |
| **Swagger UI** | http://localhost:8080/swagger-ui.html |

## Executar testes

```bash
mvn test
```

Esperado: testes MVP + novos testes de aceite/recusa passando.

## Cenário 1 — Aceitar troca válida (browser)

Objetivo: SC-001, SC-006 — fluxo MVP + aceite em menos de 7 minutos.

### Setup

1. Cadastre **Alice** e **Bob**.
2. Selecione **Alice**; adicione figurinha **10** (2×) e **25** (1×).
3. Selecione **Bob**; adicione figurinha **50** (1×).
4. Como Alice, crie proposta: oferece **10**, deseja **50**, destinatário Bob.
5. Confirme status **PROPOSED** e coleções inalteradas.

### Aceite

1. Selecione **Bob** como usuário ativo.
2. Na seção **Trocas recebidas**, localize a proposta de Alice.
3. Clique **Aceitar**.
4. Verifique:
   - Status **ACCEPTED**
   - Coleção Alice: figurinha 10 qty 1, figurinha 25 qty 1, figurinha 50 qty 1
   - Coleção Bob: figurinha 10 qty 1, figurinha 50 qty 0 (ausente)

### Regressão MVP

1. Crie nova proposta como Alice — deve nascer **PROPOSED** sem alterar coleções.
2. Fluxos de cadastro, coleção e repetidas continuam funcionando.

## Cenário 2 — Recusar troca (browser)

1. Repita setup com proposta PROPOSED entre Alice e Bob.
2. Como Bob, clique **Recusar**.
3. Verifique status **REJECTED** e coleções **idênticas** ao passo anterior.

## Cenário 3 — Erros de negócio (browser ou Swagger)

| Ação | Resultado esperado |
|------|-------------------|
| Alice tenta aceitar proposta endereçada a Bob | Erro 403 — apenas destinatário |
| Aceitar proposta já ACCEPTED | Erro 409 — proposta finalizada |
| Aceitar quando Bob não possui figurinha desejada | Erro 400 — status permanece PROPOSED |
| Aceitar proposta com oferecida = desejada | Erro 400 — troca injusta |

## Cenário 4 — Swagger (FR-025)

1. Abra http://localhost:8080/swagger-ui.html
2. Teste `POST /api/trades/{tradeId}/accept` e `POST /api/trades/{tradeId}/reject`
3. Teste `GET /api/trades/{tradeId}` para consultar status
4. Confirme endpoints MVP inalterados

## Cenário 5 — Proteção de figurinha única (opcional)

1. Em `application.yml`, defina `album.trade.protect-single-sticker: true`
2. Reinicie o backend
3. Crie proposta onde solicitante possui qty 1 da oferecida
4. Tente aceitar — deve falhar com mensagem sobre figurinha única
5. Restaure `false` para compatibilidade padrão

## Checklist de conclusão

- [ ] Aceite válido atualiza coleções e status ACCEPTED
- [ ] Recusa não altera coleções; status REJECTED
- [ ] Proposta finalizada não aceita nova ação
- [ ] Falha de validação no aceite mantém PROPOSED
- [ ] Interface exibe propostas recebidas com ações corretas
- [ ] Todos os testes `mvn test` passam
- [ ] Fluxo MVP completo sem regressão
