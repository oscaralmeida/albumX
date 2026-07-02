# Quickstart: Evolução 2 — Sugestão de Trocas e Ranking

**Feature**: `003-trade-suggestions-ranking`  
**Branch de implementação**: `003-trade-suggestions-ranking`  
**Plano**: [plan.md](./plan.md) | **Contrato**: [contracts/openapi.yaml](./contracts/openapi.yaml)  
**Depende de**: Evolução 1 estável ([../002-trade-accept-reject/quickstart.md](../002-trade-accept-reject/quickstart.md))

## Pré-requisitos

- MVP e Evolução 1 entregues (usuários, coleção, repetidas, propostas, aceite/recusa).
- Docker Desktop **ou** JDK 21 + Maven + Node.js 20.

## Execução

```bash
# Na raiz do repositório, branch 003-trade-suggestions-ranking
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

Esperado: testes MVP + Evolução 1 + novos testes de sugestões e ranking passando.

## Cenário 1 — Sugestão automática mútua (browser)

Objetivo: SC-001, SC-002 — sugestão válida sem alterar coleções.

### Setup

1. Cadastre **Alice** e **Bob**.
2. Como **Alice**: adicione figurinha **10** (2×) — não adicione **25**.
3. Como **Bob**: adicione figurinha **25** (2×) — não adicione **10**.

### Consulta

1. Selecione **Alice** como usuário ativo.
2. Acesse a aba **Sugestões**.
3. Verifique sugestão: Alice oferece **10**, recebe **25** de Bob.
4. Confirme que coleções de Alice e Bob **não mudaram**.
5. Selecione **Bob** e repita — mesma oportunidade na perspectiva de Bob (oferece 25, recebe 10).

## Cenário 2 — Sem sugestão unilateral (browser ou API)

1. Cadastre **Carlos** com figurinha **50** (2×) apenas.
2. Alice (sem figurinha 50) consulta sugestões com Carlos.
3. Se Carlos não possui repetida que Alice precise, **não** deve haver sugestão Alice↔Carlos.

## Cenário 3 — Criar proposta a partir de sugestão (browser)

Objetivo: SC-004 — ponte sugestão → proposta PROPOSED.

1. Com sugestão Alice↔Bob visível na aba **Sugestões**.
2. Clique **Criar proposta** na sugestão.
3. Verifique aba **Trocas** com formulário pré-preenchido (Alice, Bob, 10, 25).
4. Confirme criação — status **PROPOSED**, coleções inalteradas na criação.
5. Como Bob, aceite a proposta (fluxo Evolução 1).
6. Verifique coleções atualizadas após aceite.

## Cenário 4 — Ranking de usuários (browser)

Objetivo: SC-005 — ordenação por progresso e desempate.

### Setup

1. **Alice**: 335 figurinhas únicas (ou ajuste proporcional se `sticker-count` ≠ 670).
2. **Bob**: 200 figurinhas únicas.
3. Aceite pelo menos uma troca envolvendo Alice; nenhuma para Bob (ou menos que Alice).

### Consulta

1. Acesse aba **Ranking**.
2. Verifique Alice acima de Bob (maior % conclusão).
3. Se empate em %, usuário com mais trocas aceitas fica acima.
4. Após aceitar nova troca, recarregue ranking — contadores atualizados.

## Cenário 5 — Swagger (FR-026)

1. Abra http://localhost:8080/swagger-ui.html
2. Teste `GET /api/users/{userId}/trade-suggestions`
3. Teste `GET /api/ranking`
4. (Opcional) `GET /api/trade-suggestions` — visão global

## Cenário 6 — Regressão MVP + Evolução 1

1. Execute fluxo completo do [quickstart Evolução 1](../002-trade-accept-reject/quickstart.md) cenários 1–3.
2. Confirme cadastro, coleção, repetidas, criar/aceitar/recusar propostas inalterados.

## Critérios de sucesso rápidos

| ID | Verificação |
|----|-------------|
| SC-001 | Sugestões respeitam benefício mútuo e troca justa |
| SC-002 | Consultas não alteram coleções nem criam propostas |
| SC-003 | Sugestões e ranking visíveis no browser em < 2 min |
| SC-004 | Proposta a partir de sugestão em < 3 min |
| SC-005 | Ranking ordena corretamente com desempate |
| SC-007 | Zero regressão nos fluxos anteriores |
| SC-008 | Demonstração completa em sessão < 10 min |
