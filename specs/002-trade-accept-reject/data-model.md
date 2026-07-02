# Data Model: Evolução 1 — Aceitar e Recusar Trocas

**Feature**: `002-trade-accept-reject`  
**Date**: 2026-06-26  
**Spec**: [spec.md](./spec.md)  
**Base**: [../001-sticker-trade-mvp/data-model.md](../001-sticker-trade-mvp/data-model.md)

## Visão geral das mudanças

```text
UserCollection                          TradeProposal
  + removeSticker(n)                      status: PROPOSED → ACCEPTED | REJECTED
  + (via service) getQuantity             (transições irreversíveis)
  + (via service) hasDuplicate

CollectionService                       TradeService
  + removeSticker                         + acceptProposal(id, targetUserId)
  + getQuantity                           + rejectProposal(id, targetUserId)
  + hasDuplicate                          + getProposal(id)
  + (interno) transfer on accept          + validateFairTrade
                                          + validateSingleStickerProtection (config)
```

Entidades `User`, `Album`, `CollectionEntry` permanecem inalteradas em estrutura.

## TradeStatus (enum) — ativação Evolução 1

| Valor | Descrição | Atribuído quando |
|-------|-----------|------------------|
| `PROPOSED` | Proposta registrada | Criação (MVP); mantido se aceite falhar validação |
| `ACCEPTED` | Troca efetivada | Após transferência bem-sucedida das coleções |
| `REJECTED` | Troca recusada | Destinatário recusa; coleções inalteradas |

**Regra**: estados `ACCEPTED` e `REJECTED` são finais — nenhuma transição de volta para `PROPOSED` (FR-004).

## Transições de estado (TradeProposal)

```text
                    ┌── validações OK + coleções atualizadas ──▶ ACCEPTED
  (criação) ──▶ PROPOSED ──┤
                    │                                      (estados finais)
                    └── destinatário recusa ────────────────▶ REJECTED

  PROPOSED ── aceite com falha de validação ──▶ PROPOSED (sem alteração)
  ACCEPTED / REJECTED ── qualquer aceite/recusa ──▶ rejeitado (409)
```

## UserCollection — novas operações de domínio

| Operação | Comportamento | Pré-condição |
|----------|---------------|--------------|
| `removeSticker(number)` | Decrementa qty em 1; remove entrada se qty = 0 | qty ≥ 1; senão exceção |
| `getQuantity(number)` | Retorna qty (0 se ausente) | — |
| `hasSticker(number)` | qty ≥ 1 | (existente) |
| `hasDuplicate(number)` | qty > 1 | — |

**Invariante após aceite**: quantidades nunca negativas; entrada ausente ≡ qty 0.

## CollectionService — novos métodos

| Método | Responsabilidade | Exceção |
|--------|------------------|---------|
| `removeSticker(userId, number)` | Delega a `UserCollection.removeSticker`; persiste | `StickerNotOwnedException` se qty = 0 |
| `getQuantity(userId, number)` | Retorna quantidade atual | `UserNotFoundException` |
| `hasDuplicate(userId, number)` | `getQuantity > 1` | `UserNotFoundException` |
| `addSticker` | (existente) incremento | — |

**Efeito do aceite** (FR-008, FR-009):

```text
Solicitante:  removeSticker(offered);  addSticker(requested)
Destinatário: removeSticker(requested); addSticker(offered)
```

Ordem sugerida dentro da transação: validar ambas as posses → executar 4 mutações → salvar proposta.

## TradeService — novos métodos e validações

| Método | Descrição |
|--------|-----------|
| `acceptProposal(tradeId, targetUserId)` | Fluxo completo de aceite |
| `rejectProposal(tradeId, targetUserId)` | Marca REJECTED sem tocar coleções |
| `getProposal(tradeId)` | Consulta proposta por ID |

### Validações no aceite (ordem)

| ID | Validação | Onde | Erro |
|----|-----------|------|------|
| V-07 | Proposta existe | TradeService | 404 `TRADE_NOT_FOUND` |
| V-08 | Status = PROPOSED | TradeService | 409 `TRADE_ALREADY_FINALIZED` |
| V-09 | `targetUserId` = destinatário da proposta | TradeService | 403 `UNAUTHORIZED_TRADE_ACTION` |
| V-10 | Solicitante possui oferecida (qty ≥ 1) | TradeService + CollectionService | 400 `STICKER_NOT_OWNED` |
| V-11 | Destinatário possui desejada (qty ≥ 1) | TradeService + CollectionService | 400 `STICKER_NOT_OWNED` |
| V-12 | Oferecida ≠ desejada (troca justa) | TradeService | 400 `UNFAIR_TRADE` |
| V-13 | Proteção figurinha única (se config ativa) | TradeService | 400 `SINGLE_STICKER_PROTECTED` |

### Validações na recusa

| ID | Validação | Erro |
|----|-----------|------|
| V-07 | Proposta existe | 404 |
| V-08 | Status = PROPOSED | 409 |
| V-09 | Actor = destinatário | 403 |

**Nota**: recusa não valida posse de figurinhas (FR-010).

## Configuração — TradeRules

| Propriedade | Tipo | Padrão | Descrição |
|-------------|------|--------|-----------|
| `album.trade.protect-single-sticker` | boolean | `false` | Impede aceite se qty = 1 da figurinha entregue por qualquer parte |

```yaml
album:
  sticker-count: 700
  trade:
    protect-single-sticker: false
```

## TradeProposal — campos inalterados

Persistência JPA existente suporta novos valores de `status` sem migração (enum string).

## Validações consolidadas (MVP + Evolução 1)

| ID | Validação | Fase | HTTP |
|----|-----------|------|------|
| V-01..V-06 | (MVP) | MVP | — |
| V-07 | Proposta existe | E1 | 404 |
| V-08 | Proposta não finalizada | E1 | 409 |
| V-09 | Apenas destinatário age | E1 | 403 |
| V-10 | Solicitante possui oferecida no aceite | E1 | 400 |
| V-11 | Destinatário possui desejada no aceite | E1 | 400 |
| V-12 | Troca justa (números distintos) | E1 | 400 |
| V-13 | Proteção figurinha única (opcional) | E1 | 400 |

## Modelo físico

Sem alteração de schema. Tabela `trade_proposals.status` passa a armazenar `ACCEPTED` e `REJECTED`.
