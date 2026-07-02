# Feature Specification: Evolução 1 — Aceitar e Recusar Trocas

**Feature Branch**: `002-trade-accept-reject`

**Created**: 2026-06-26

**Status**: Draft

**Input**: User description: "Evolução 1 do sistema de troca de figurinhas da Copa: permitir que o destinatário aceite ou recuse propostas de troca, com validação de regras de negócio para troca consistente e justa; incluir evolução da interface web para visualizar propostas recebidas, aceitar, recusar e exibir status (PROPOSED, ACCEPTED, REJECTED)."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Aceitar proposta de troca válida (Priority: P1)

Como colecionador destinatário de uma proposta de troca, quero aceitar a proposta recebida, para que a troca seja efetivada e minha coleção (e a do solicitante) seja atualizada corretamente.

**Why this priority**: Aceitar troca é o núcleo da Evolução 1 — transforma propostas registradas no MVP em trocas reais que alteram as coleções.

**Independent Test**: Pode ser testado com dois usuários, coleções compatíveis e uma proposta em status PROPOSED; ao aceitar como destinatário, verificar status ACCEPTED e quantidades corretas nas duas coleções.

**Acceptance Scenarios**:

1. **Given** usuário A possui figurinha 10 (qty ≥ 1), usuário B possui figurinha 25 (qty ≥ 1) e existe proposta PROPOSED de A para B (oferece 10, deseja 25), **When** B aceita a proposta, **Then** o status passa a ACCEPTED, A perde uma unidade de 10 e ganha uma unidade de 25, e B perde uma unidade de 25 e ganha uma unidade de 10.
2. **Given** proposta PROPOSED válida, **When** o destinatário aceita, **Then** ambas as coleções refletem as quantidades finais esperadas (solicitante: −1 oferecida, +1 desejada; destinatário: −1 desejada, +1 oferecida).
3. **Given** proposta PROPOSED, **When** o solicitante (não o destinatário) tenta aceitar, **Then** o sistema rejeita a operação e informa que apenas o destinatário pode aceitar.
4. **Given** proposta com status ACCEPTED ou REJECTED, **When** alguém tenta aceitar novamente, **Then** o sistema rejeita a operação e informa que a proposta já está finalizada.

---

### User Story 2 - Recusar proposta de troca (Priority: P2)

Como colecionador destinatário de uma proposta de troca, quero recusar a proposta recebida, para registrar que não concordo com a troca sem alterar minha coleção nem a do solicitante.

**Why this priority**: Recusar é o caminho alternativo essencial ao aceite; completa o ciclo de vida da proposta sem efeitos colaterais nas coleções.

**Independent Test**: Pode ser testado criando proposta PROPOSED, recusando como destinatário e verificando status REJECTED com coleções inalteradas.

**Acceptance Scenarios**:

1. **Given** proposta PROPOSED de A para B, **When** B recusa a proposta, **Then** o status passa a REJECTED e as coleções de A e B permanecem iguais às anteriores.
2. **Given** proposta PROPOSED, **When** o solicitante tenta recusar, **Then** o sistema rejeita a operação e informa que apenas o destinatário pode recusar.
3. **Given** proposta com status ACCEPTED, **When** alguém tenta recusar, **Then** o sistema rejeita a operação e informa que a proposta já está finalizada.
4. **Given** proposta com status REJECTED, **When** alguém tenta recusar novamente, **Then** o sistema rejeita a operação e informa que a proposta já está finalizada.

---

### User Story 3 - Validação de regras de negócio na efetivação (Priority: P3)

Como colecionador, quero que o sistema valide propriedade das figurinhas, justiça da troca e disponibilidade no momento do aceite, para que trocas inconsistentes ou injustas não sejam efetivadas.

**Why this priority**: Garante integridade do domínio e confiança nas trocas; complementa o aceite com regras que o MVP adiou.

**Independent Test**: Pode ser testado tentando aceitar propostas inválidas (figurinha indisponível, troca injusta, figurinha única quando regra ativa) e verificando rejeição com mensagem clara e coleções inalteradas.

**Acceptance Scenarios**:

1. **Given** proposta PROPOSED em que o solicitante não possui mais a figurinha oferecida (quantidade 0), **When** o destinatário tenta aceitar, **Then** o sistema rejeita, mantém status PROPOSED e não altera coleções.
2. **Given** proposta PROPOSED em que o destinatário não possui a figurinha solicitada (quantidade 0), **When** o destinatário tenta aceitar, **Then** o sistema rejeita, mantém status PROPOSED e não altera coleções.
3. **Given** proposta PROPOSED com figurinha oferecida igual à figurinha desejada (mesmo número), **When** o destinatário tenta aceitar, **Then** o sistema rejeita por troca injusta e não altera coleções.
4. **Given** regra de proteção de figurinha única ativa, proposta PROPOSED em que o solicitante possui quantidade 1 da figurinha oferecida, **When** o destinatário tenta aceitar, **Then** o sistema rejeita e informa que não é permitido trocar a única unidade.
5. **Given** regra de proteção de figurinha única ativa, proposta PROPOSED em que o destinatário possui quantidade 1 da figurinha solicitada, **When** tenta aceitar, **Then** o sistema rejeita e informa que não é permitido trocar a única unidade.
6. **Given** regra de proteção de figurinha única desativada, proposta PROPOSED em que ambos possuem quantidade 1 das respectivas figurinhas envolvidas, **When** o destinatário aceita, **Then** a troca é efetivada normalmente (compatível com comportamento do MVP na criação de propostas).

---

### User Story 4 - Visualizar e responder propostas na interface web (Priority: P4)

Como colecionador usando a interface web, quero visualizar propostas de troca recebidas, ver o status de cada proposta e aceitar ou recusar diretamente no navegador, para concluir trocas sem ferramentas externas.

**Why this priority**: Atende ao Princípio VIII da constituição (demonstrabilidade visual) e torna a Evolução 1 utilizável em contexto de treinamento.

**Independent Test**: Pode ser testado acessando a página de trocas no navegador, selecionando um usuário destinatário, visualizando propostas recebidas com status e acionando aceitar ou recusar com feedback visual.

**Acceptance Scenarios**:

1. **Given** usuário B com propostas recebidas em vários status, **When** B acessa a interface de trocas, **Then** vê propostas endereçadas a B com status PROPOSED, ACCEPTED ou REJECTED claramente indicado.
2. **Given** proposta PROPOSED recebida por B, **When** B clica em aceitar na interface web, **Then** a proposta passa a ACCEPTED, as coleções são atualizadas e a interface reflete o novo status.
3. **Given** proposta PROPOSED recebida por B, **When** B clica em recusar na interface web, **Then** a proposta passa a REJECTED, as coleções não mudam e a interface reflete o novo status.
4. **Given** tentativa de aceitar proposta inválida pela interface web, **When** a operação falha, **Then** o usuário vê mensagem de erro compreensível sem quebrar a página.
5. **Given** proposta já finalizada (ACCEPTED ou REJECTED), **When** visualizada na interface, **Then** ações de aceitar/recusar não estão disponíveis ou estão desabilitadas.

---

### User Story 5 - Documentação interativa e compatibilidade com MVP (Priority: P5)

Como desenvolvedor ou instrutor, quero consultar e testar aceite/recusa de trocas pela documentação interativa existente, mantendo todas as capacidades do MVP, para validar a evolução de forma incremental.

**Why this priority**: Preserva o fluxo didático do projeto e garante que a Evolução 1 estenda o MVP sem regressões.

**Independent Test**: Pode ser testado executando fluxo MVP completo (cadastro → coleção → repetidas → criar proposta) seguido de aceite ou recusa via documentação interativa, verificando comportamento preservado e novos endpoints funcionais.

**Acceptance Scenarios**:

1. **Given** aplicação em execução, **When** operador consulta documentação interativa, **Then** operações de aceitar e recusar proposta estão descritas e testáveis.
2. **Given** fluxo MVP de criação de proposta, **When** executado após esta evolução, **Then** comportamento permanece equivalente (proposta criada com status PROPOSED, coleções inalteradas na criação).
3. **Given** proposta aceita com sucesso, **When** consultadas coleções dos envolvidos, **Then** quantidades refletem a troca efetivada.

---

### Edge Cases

- O que acontece se, entre a criação e o aceite, o solicitante registra mais figurinhas ou consome a oferecida em outra operação? Na aceitação, o sistema revalida posse atual; se indisponível, rejeita sem alterar status para ACCEPTED.
- O que acontece se o destinatário perde a figurinha solicitada antes do aceite? Aceitação rejeitada; proposta permanece PROPOSED.
- Duas propostas PROPOSED concorrentes podem consumir a mesma figurinha? A primeira aceita consome a unidade; a segunda falha na validação de posse ao tentar aceitar.
- Proposta com usuário solicitante ou destinatário removido/inexistente? Operações de aceite/recusa rejeitadas (usuário ou proposta inválida).
- Identificador de proposta inexistente? Operação rejeitada com mensagem clara.
- Troca aceita quando uma figurinha envolvida tinha quantidade exatamente 1 e regra de figurinha única desativada? Permitida; após aceite, quem entregou fica com quantidade 0 (figurinha ausente da coleção ou qty 0).
- Listagem de propostas: o solicitante continua vendo suas propostas enviadas com status atualizado após aceite/recusa pelo destinatário.
- Recusar não impede criar nova proposta entre os mesmos usuários e figurinhas.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: O sistema MUST permitir que o usuário destinatário aceite uma proposta de troca identificada, informando qual usuário destinatário está realizando a ação.
- **FR-002**: O sistema MUST permitir que o usuário destinatário recuse uma proposta de troca identificada, informando qual usuário destinatário está realizando a ação.
- **FR-003**: O sistema MUST aceitar ou recusar apenas propostas com status PROPOSED.
- **FR-004**: O sistema MUST rejeitar aceite ou recusa quando a proposta já estiver com status ACCEPTED ou REJECTED.
- **FR-005**: O sistema MUST rejeitar aceite ou recusa quando o usuário informado não for o destinatário da proposta.
- **FR-006**: Ao aceitar proposta válida, o sistema MUST alterar o status da proposta para ACCEPTED.
- **FR-007**: Ao recusar proposta válida, o sistema MUST alterar o status da proposta para REJECTED.
- **FR-008**: Ao aceitar proposta válida, o sistema MUST decrementar em 1 a quantidade da figurinha oferecida na coleção do solicitante e incrementar em 1 a quantidade da figurinha desejada na coleção do solicitante.
- **FR-009**: Ao aceitar proposta válida, o sistema MUST decrementar em 1 a quantidade da figurinha desejada na coleção do destinatário e incrementar em 1 a quantidade da figurinha oferecida na coleção do destinatário.
- **FR-010**: Ao recusar proposta, o sistema MUST NOT alterar as coleções de solicitante ou destinatário.
- **FR-011**: Antes de efetivar aceite, o sistema MUST validar que o solicitante possui quantidade ≥ 1 da figurinha oferecida.
- **FR-012**: Antes de efetivar aceite, o sistema MUST validar que o destinatário possui quantidade ≥ 1 da figurinha desejada (solicitada).
- **FR-013**: O sistema MUST considerar troca justa como intercâmbio de exatamente uma figurinha por exatamente uma figurinha com números distintos; MUST rejeitar aceite quando figurinha oferecida e figurinha desejada forem o mesmo número.
- **FR-014**: O sistema MUST suportar configuração opcional que, quando ativa, impede aceite se o solicitante possuir quantidade igual a 1 da figurinha oferecida.
- **FR-015**: O sistema MUST suportar configuração opcional que, quando ativa, impede aceite se o destinatário possuir quantidade igual a 1 da figurinha desejada.
- **FR-016**: Quando aceite for rejeitado por falha de validação, o sistema MUST manter status PROPOSED e MUST NOT alterar coleções.
- **FR-017**: O sistema MUST preservar todas as funcionalidades do MVP: cadastro de usuários, registro e consulta de coleção, consulta de repetidas e criação de propostas de troca.
- **FR-018**: O sistema MUST continuar criando novas propostas com status PROPOSED sem alterar coleções no momento da criação (comportamento MVP preservado).
- **FR-019**: O sistema MUST permitir consultar propostas de troca incluindo status (PROPOSED, ACCEPTED, REJECTED), compatível com listagens existentes estendidas.
- **FR-020**: O sistema MUST permitir filtrar ou distinguir propostas recebidas por um usuário destinatário para apoio à interface web.
- **FR-021**: A interface web MUST exibir propostas recebidas pelo usuário selecionado, com status visível (PROPOSED, ACCEPTED, REJECTED).
- **FR-022**: A interface web MUST permitir aceitar proposta PROPOSED recebida pelo usuário selecionado.
- **FR-023**: A interface web MUST permitir recusar proposta PROPOSED recebida pelo usuário selecionado.
- **FR-024**: A interface web MUST apresentar mensagens de erro compreensíveis quando aceite ou recusa violarem regras de negócio.
- **FR-025**: A documentação interativa MUST incluir as operações de aceitar e recusar proposta, testáveis no navegador.
- **FR-026**: O sistema MUST apresentar mensagens de erro compreensíveis quando proposta ou usuário referenciados forem inválidos ou inexistentes.

### Key Entities

- **Usuário**: Inalterado em relação ao MVP. Colecionador com identificador único e nome; possui uma Coleção.
- **Álbum**: Inalterado. Catálogo finito de números válidos de figurinhas.
- **Figurinha**: Inalterado. Item do álbum identificado por número.
- **Coleção**: Inalterado em estrutura. Conjunto de entradas (número + quantidade). Evolução 1 passa a ser mutável por aceite de troca (antes, apenas incrementos via registro manual no MVP).
- **Troca (proposta)**: Registro de intenção entre dois usuários. Atributos: identificador, solicitante, destinatário, figurinha oferecida, figurinha desejada, status (PROPOSED | ACCEPTED | REJECTED), data/hora de criação. Ciclo de vida: criada como PROPOSED (MVP); pode transicionar para ACCEPTED (efetiva troca) ou REJECTED (sem alterar coleções); estados finais não permitem nova transição.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% das tentativas de aceitar proposta PROPOSED válida resultam em status ACCEPTED e coleções dos dois usuários atualizadas conforme regras de negócio.
- **SC-002**: 100% das tentativas de recusar proposta PROPOSED resultam em status REJECTED sem alteração nas coleções.
- **SC-003**: 100% das tentativas de aceitar ou recusar proposta já finalizada (ACCEPTED ou REJECTED) são rejeitadas com mensagem compreensível.
- **SC-004**: 100% das tentativas de aceitar proposta em que solicitante ou destinatário não possui a figurinha exigida são rejeitadas, mantendo status PROPOSED e coleções inalteradas.
- **SC-005**: Quando a regra de figurinha única estiver ativa, 100% das tentativas de aceitar troca envolvendo entrega da única unidade de qualquer parte são rejeitadas.
- **SC-006**: Participantes do treinamento conseguem executar fluxo completo MVP + aceite ou recusa de troca exclusivamente pelo navegador em menos de 7 minutos.
- **SC-007**: Todos os critérios de aceite definidos pelo solicitante da evolução são verificáveis por cenários de teste manuais ou automatizados.
- **SC-008**: Nenhuma funcionalidade do MVP deixa de funcionar após entrega desta evolução (regressão zero nos fluxos cadastro → coleção → repetidas → criar proposta).

## Assumptions

- A Evolução 1 opera sobre o mesmo álbum único e modelo de usuários do MVP, sem autenticação — operações identificam usuários pelo identificador único do sistema (mesmo modelo didático do MVP).
- Apenas o destinatário pode aceitar ou recusar; não há papéis adicionais nem verificação de identidade além do identificador informado na operação.
- Troca justa significa exatamente 1 figurinha por 1 figurinha com números diferentes; o modelo de proposta já representa uma unidade de cada lado — a regra adicional impede aceite quando oferecida e desejada são o mesmo número.
- A regra opcional de proteção de figurinha única é configurável no sistema; **valor padrão: desativada**, preservando compatibilidade com propostas criadas no MVP que oferecem figurinha em quantidade 1. Quando ativada, aplica-se apenas na efetivação (aceite), não retroage para invalidar propostas já registradas.
- Falha de validação no aceite não altera o status da proposta (permanece PROPOSED), permitindo nova tentativa se as condições voltarem a ser satisfeitas.
- Listagem de propostas existente no MVP é estendida para suportar visualização de recebidas e status; não é obrigatório cancelamento de proposta pelo solicitante nesta evolução.
- A interface web permanece simples (formulários e listagens), sem notificações, chat ou design avançado.
- Documentação interativa e execução simplificada (ex.: comando único / container) herdadas do MVP continuam válidas e são atualizadas na fase de plan para incluir novas operações.

## Out of Scope (Evolução 1)

- Sugestão automática de trocas compatíveis.
- Ranking ou gamificação de colecionadores.
- Notificações ao destinatário sobre novas propostas ou mudanças de status.
- Chat ou mensagens entre usuários.
- Interface gráfica avançada, design elaborado ou aplicativo mobile.
- Autenticação, autorização ou perfis de acesso.
- Cancelamento de proposta pelo solicitante.
- Troca de múltiplas figurinhas em uma única proposta (N por M).
- Revalidação retroativa ou invalidação automática de propostas PROPOSED antigas (permanecem aceitáveis enquanto válidas no aceite).
- Múltiplos álbuns ou edições de Copa simultâneas.

## Dependencies

- **MVP (001-sticker-trade-mvp)**: Cadastro de usuários, coleções, repetidas, criação de propostas PROPOSED e infraestrutura de demonstração (interface web, documentação interativa, execução simplificada) devem estar entregues e estáveis.
- **Constituição do projeto**: Regras de domínio centralizadas, testes de domínio e demonstrabilidade visual (Princípios IV, VII e VIII) aplicam-se a esta evolução.
