# Data Model: Evolução 2 — Sugestão de Trocas e Ranking

**Feature**: `003-trade-suggestions-ranking`  
**Date**: 2026-07-02  
**Spec**: [spec.md](./spec.md)  
**Base**: [../002-trade-accept-reject/data-model.md](../002-trade-accept-reject/data-model.md)

## Visão geral das mudanças

```text
UserCollection                          (sem novas entidades persistentes)
  + getUniqueStickerNumbers()           TradeSuggestion (value object, efêmero)
  + getMissingStickerNumbers(albumSize)   userAId, userBId
                                          stickerOfferedByUserA, stickerOfferedByUserB
CollectionService                         reason
  + getUniqueStickerCount
  + getMissingStickerNumbers
  + canOfferSticker                       UserRankingPosition (value object, efêmero)
                                          userId, userName, uniqueStickersCount,
TradeSuggestionService                    albumCompletionPercentage,
  + findSuggestionsForUser(userId)        acceptedTradesCount, position
  + findAllSuggestions() (opcional)

RankingService
  + getRanking()

TradeProposalRepository
  + countAcceptedByUserId(userId)

UserRepository
  + findAll()
```

Entidades persistentes `User`, `Album`, `CollectionEntry`, `TradeProposal` permanecem inalteradas em schema.

## TradeSuggestion (value object)

Oportunidade calculada entre dois usuários. **Não persistida.**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `userAId` | UUID | Usuário na perspectiva do solicitante (usuário consultado) |
| `userBId` | UUID | Parceiro da troca |
| `stickerOfferedByUserA` | int | Figurinha que A pode oferecer (repetida/oferecível) |
| `stickerOfferedByUserB` | int | Figurinha que B pode oferecer (= o que A receberia) |
| `reason` | String | Texto didático, ex.: "Troca mútua: A oferece #10, B oferece #25" |

**Invariantes**:
- `userAId ≠ userBId`
- `stickerOfferedByUserA ≠ stickerOfferedByUserB`
- A pode oferecer `stickerOfferedByUserA` (regra de oferta + B não possui)
- B pode oferecer `stickerOfferedByUserB` (regra de oferta + A não possui)

## UserRankingPosition (value object)

Entrada calculada no ranking. **Não persistida.**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `userId` | UUID | Identificador do usuário |
| `userName` | String | Nome para exibição |
| `uniqueStickersCount` | int | Figurinhas distintas com qty ≥ 1 |
| `albumCompletionPercentage` | double | `(unique / albumTotal) * 100` |
| `acceptedTradesCount` | int | Propostas ACCEPTED onde usuário participou |
| `position` | int | Posição ordinal (1 = melhor), atribuída após ordenação |

## UserCollection — novas operações

| Operação | Comportamento |
|----------|---------------|
| `getUniqueStickerNumbers()` | Lista números com `getQuantity(n) >= 1`, ordenados |
| `getMissingStickerNumbers(albumSize)` | Números de 1..albumSize ausentes ou qty 0 |

## CollectionService — novos métodos

| Método | Responsabilidade |
|--------|------------------|
| `getUniqueStickerCount(userId)` | Tamanho de figurinhas únicas |
| `getMissingStickerNumbers(userId)` | Ausências no álbum |
| `canOfferSticker(userId, number, protectSingleSticker)` | `true` se qty atende regra de oferta |

**Regra `canOfferSticker`**:
- Se `protectSingleSticker`: `qty >= 2`
- Senão: `qty >= 1`

## TradeSuggestionService — algoritmo

```text
findSuggestionsForUser(userAId):
  ensureUserExists(userAId)
  suggestions = []
  for each userB in allUsers where userB != userA:
    for each stickerX in offerableBy(userA, userB):   // A oferece, B não tem
      for each stickerY in offerableBy(userB, userA): // B oferece, A não tem
        if stickerX != stickerY:
          suggestions.add(TradeSuggestion(userA, userB, stickerX, stickerY, reason))
  return suggestions

offerableBy(owner, partner):
  stickers where canOfferSticker(owner, n) AND partner.getQuantity(n) == 0
```

**Garantias**: nenhuma chamada a `save` em coleções ou propostas.

## RankingService — algoritmo

```text
getRanking():
  positions = []
  for each user in allUsers:
    unique = collectionService.getUniqueStickerCount(user.id)
    pct = (unique / album.stickerCount) * 100
    trades = tradeRepo.countAcceptedByUserId(user.id)
    positions.add(UserRankingPosition(...))
  sort by pct DESC, trades DESC, userId ASC
  assign position 1..n
  return positions
```

## TradeProposalRepository — novo método

| Método | Descrição |
|--------|-----------|
| `countAcceptedByUserId(UUID userId)` | COUNT onde `status = ACCEPTED` AND (`requesterUserId = userId` OR `targetUserId = userId`) |

## UserRepository — novo método

| Método | Descrição |
|--------|-----------|
| `findAll()` | Lista todos os usuários cadastrados |

## Validações consolidadas (Evolução 2)

| ID | Validação | Onde | Erro |
|----|-----------|------|------|
| V-20 | Usuário existe (sugestões) | TradeSuggestionService | 404 `USER_NOT_FOUND` |
| V-21 | Benefício mútuo | TradeSuggestionService (algoritmo) | (sugestão omitida) |
| V-22 | Troca justa (números distintos) | TradeSuggestionService | (sugestão omitida) |
| V-23 | Sem auto-troca | TradeSuggestionService | (par ignorado) |
| V-24 | Proteção figurinha única nas ofertas | TradeSuggestionService via `canOfferSticker` | (sugestão omitida) |
| V-25 | Consultas read-only | TradeSuggestionService, RankingService | nenhuma mutação |

Criação de proposta a partir de sugestão na UI reutiliza validações MVP/Evolução 1 em `TradeService.createProposal` — sem novas regras no backend.

## DTOs de aplicação (API)

| DTO | Campos expostos |
|-----|-----------------|
| `TradeSuggestionResponse` | `requesterUserId`, `partnerUserId`, `offeredStickerNumber`, `requestedStickerNumber`, `reason` |
| `UserRankingResponse` | `position`, `userId`, `userName`, `uniqueStickersCount`, `albumCompletionPercentage`, `acceptedTradesCount` |

Mapeamento: `userAId` → `requesterUserId`; `userBId` → `partnerUserId`; `stickerOfferedByUserA` → `offeredStickerNumber`; `stickerOfferedByUserB` → `requestedStickerNumber`.

## Compatibilidade

- Nenhuma migração de banco.
- Endpoints MVP e Evolução 1 inalterados.
- Config `album.sticker-count` e `album.trade.protect-single-sticker` reutilizadas.
