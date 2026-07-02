# Feature Specification: Evolução 2 — Sugestão de Trocas e Ranking de Usuários

**Feature Branch**: `003-trade-suggestions-ranking`

**Created**: 2026-07-02

**Status**: Draft

**Input**: User description: "Evolução 2 do sistema de troca de figurinhas da Copa: adicionar sugestão automática de trocas e ranking de usuários, com inteligência e engajamento, mantendo compatibilidade com MVP e Evolução 1. Incluir evolução da interface web para exibir sugestões, permitir criar proposta a partir de sugestão e exibir ranking."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Consultar sugestões automáticas de troca (Priority: P1)

Como colecionador, quero consultar sugestões de trocas em que eu e outro usuário possamos nos beneficiar mutuamente, para descobrir oportunidades de completar o álbum sem precisar analisar manualmente todas as coleções.

**Why this priority**: A sugestão automática é o núcleo da Evolução 2 — entrega valor de inteligência sobre os dados já existentes e prepara o fluxo de criação de propostas.

**Independent Test**: Pode ser testado com dois ou mais usuários com coleções complementares (repetidas de um lado, ausências do outro); ao consultar sugestões para um usuário, verificar que retornam pares válidos com figurinhas oferecidas e desejadas corretas, sem alterar coleções nem criar propostas.

**Acceptance Scenarios**:

1. **Given** usuário A possui figurinha 10 em quantidade ≥ 2 e não possui figurinha 25; usuário B possui figurinha 25 em quantidade ≥ 2 e não possui figurinha 10, **When** A consulta suas sugestões de troca, **Then** o sistema retorna ao menos uma sugestão indicando A, B, figurinha 10 (oferecida por A) e figurinha 25 (oferecida por B).
2. **Given** usuário A consulta sugestões, **When** a consulta é processada, **Then** nenhuma coleção é alterada e nenhuma proposta de troca é criada automaticamente.
3. **Given** usuário A sem figurinhas repetidas ou sem parceiro compatível, **When** consulta sugestões, **Then** o sistema retorna lista vazia.
4. **Given** sugestão válida entre A e B, **When** B consulta suas sugestões, **Then** a mesma oportunidade aparece do ponto de vista de B (com papéis e figurinhas coerentes com a perspectiva de B).
5. **Given** identificador de usuário inexistente, **When** alguém consulta sugestões para esse usuário, **Then** o sistema rejeita a operação com mensagem compreensível.

---

### User Story 2 - Regras de negócio nas sugestões (Priority: P2)

Como colecionador, quero que as sugestões respeitem troca justa, benefício mútuo e proteção de figurinha única, para que apenas oportunidades realmente viáveis e seguras sejam apresentadas.

**Why this priority**: Garante confiança nas sugestões e alinhamento com as regras de domínio já estabelecidas na Evolução 1.

**Independent Test**: Pode ser testado montando coleções que violam regras (sem repetida, figurinha única, mesmo número nos dois lados) e verificando que tais combinações não aparecem nas sugestões.

**Acceptance Scenarios**:

1. **Given** A possui repetida de 10 e B não possui 10, mas B não possui repetida de nenhuma figurinha que A precise, **When** A consulta sugestões, **Then** não há sugestão entre A e B.
2. **Given** A e B poderiam trocar figurinhas 10 e 25, mas ambos ofereceriam figurinhas em quantidade 1 apenas, **When** a regra de proteção de figurinha única está ativa, **Then** essa combinação não aparece nas sugestões.
3. **Given** regra de figurinha única desativada, A e B com quantidade 1 das respectivas figurinhas oferecidas e ausência da figurinha desejada no outro lado, **When** consultam sugestões, **Then** a oportunidade pode aparecer (compatível com comportamento da Evolução 1).
4. **Given** potencial troca em que figurinha oferecida por A e figurinha oferecida por B seriam o mesmo número, **When** consultam sugestões, **Then** essa combinação não aparece (troca injusta).
5. **Given** A possui repetida de 10, B não possui 10, B possui repetida de 10 também (impossível — B não pode ter repetida do que não tem), **When** consultam sugestões, **Then** cenários inconsistentes são excluídos; apenas pares em que cada lado oferece repetida e o outro não possui a figurinha desejada são sugeridos.
6. **Given** A e B são o mesmo usuário, **When** consultam sugestões, **Then** não há sugestão envolvendo o próprio usuário.

---

### User Story 3 - Criar proposta a partir de sugestão na interface web (Priority: P3)

Como colecionador usando a interface web, quero utilizar uma sugestão exibida como base para criar uma proposta de troca, para converter uma oportunidade identificada pelo sistema em negociação real com um clique ou poucos passos.

**Why this priority**: Fecha o ciclo de valor da sugestão — sem essa ponte, a inteligência permanece apenas informativa.

**Independent Test**: Pode ser testado exibindo sugestões na interface, acionando ação de criar proposta a partir de uma sugestão e verificando que o formulário de proposta é preenchido corretamente e que a proposta criada segue o fluxo existente (status PROPOSED, coleções inalteradas na criação).

**Acceptance Scenarios**:

1. **Given** sugestão exibida para usuário A (oferece 10, recebe 25 de B), **When** A aciona criar proposta a partir da sugestão, **Then** a interface inicia criação de proposta com solicitante A, destinatário B, figurinha oferecida 10 e figurinha desejada 25 pré-preenchidos ou confirmáveis.
2. **Given** proposta criada a partir de sugestão, **When** confirmada, **Then** comportamento é idêntico ao fluxo de criação de proposta do MVP/Evolução 1 (validações de posse, status PROPOSED, sem alteração de coleções na criação).
3. **Given** sugestão exibida, **When** o usuário apenas visualiza sem criar proposta, **Then** nenhuma proposta é registrada.
4. **Given** tentativa de criar proposta a partir de sugestão com dados que deixaram de ser válidos (ex.: repetida consumida), **When** confirma criação, **Then** o sistema aplica validações normais de criação de proposta e informa erro compreensível.

---

### User Story 4 - Consultar ranking de usuários (Priority: P4)

Como colecionador, quero consultar um ranking dos usuários do sistema, para identificar quem está mais avançado na coleção ou mais ativo em trocas aceitas.

**Why this priority**: Entrega engajamento e visibilidade do progresso coletivo; complementa as sugestões sem depender delas.

**Independent Test**: Pode ser testado com três ou mais usuários com coleções e histórico de trocas distintos; verificar ordenação por percentual de conclusão e desempate por trocas aceitas, sem alteração de dados.

**Acceptance Scenarios**:

1. **Given** usuário X com 335 figurinhas únicas em álbum de 670 e usuário Y com 200 figurinhas únicas, **When** consulta o ranking, **Then** X aparece melhor posicionado que Y (maior percentual de conclusão).
2. **Given** usuários X e Y com mesmo percentual de conclusão, X com 5 trocas aceitas e Y com 2, **When** consulta o ranking, **Then** X aparece acima de Y.
3. **Given** consulta de ranking, **When** processada, **Then** nenhum dado de usuário, coleção ou proposta é alterado.
4. **Given** usuário sem figurinhas na coleção, **When** aparece no ranking, **Then** percentual de conclusão é 0% e posição reflete isso.
5. **Given** usuário com coleção mas sem trocas aceitas, **When** aparece no ranking, **Then** quantidade de trocas aceitas é 0 e desempate usa esse valor.
6. **Given** nenhum usuário cadastrado, **When** consulta ranking, **Then** retorna lista vazia.

---

### User Story 5 - Visualizar sugestões e ranking na interface web (Priority: P5)

Como participante de treinamento, quero ver sugestões de troca e ranking de usuários na interface web, para demonstrar as capacidades inteligentes do sistema sem ferramentas externas.

**Why this priority**: Atende ao Princípio VIII da constituição (demonstrabilidade visual) e torna a Evolução 2 utilizável em contexto didático.

**Independent Test**: Pode ser testado acessando as novas seções no navegador, selecionando usuário para sugestões, visualizando ranking e criando proposta a partir de sugestão.

**Acceptance Scenarios**:

1. **Given** aplicação em execução com usuários e coleções, **When** acessa a seção de sugestões na interface web, **Then** vê lista de sugestões para o usuário selecionado com usuário parceiro, figurinha oferecida e figurinha desejada claramente indicados.
2. **Given** aplicação em execução, **When** acessa a seção de ranking na interface web, **Then** vê lista ordenada com nome ou identificador do usuário, percentual de conclusão e quantidade de trocas aceitas (e demais métricas definidas).
3. **Given** lista de sugestões vazia, **When** exibida na interface, **Then** mensagem ou estado vazio compreensível é mostrado sem erro.
4. **Given** erro ao carregar sugestões ou ranking, **When** ocorre falha, **Then** usuário vê mensagem de erro compreensível sem quebrar a página.

---

### User Story 6 - Compatibilidade com MVP e Evolução 1 (Priority: P6)

Como desenvolvedor ou instrutor, quero que todas as funcionalidades anteriores continuem operando após a Evolução 2, e que as novas operações estejam disponíveis na documentação interativa, para validar evolução incremental sem regressões.

**Why this priority**: Preserva o fluxo didático e a estabilidade exigida pela constituição (Princípio VI).

**Independent Test**: Pode ser testado executando fluxo completo MVP + Evolução 1 (cadastro → coleção → repetidas → proposta → aceite/recusa) seguido de consulta de sugestões e ranking, verificando comportamento preservado.

**Acceptance Scenarios**:

1. **Given** aplicação após Evolução 2, **When** executa fluxo MVP e Evolução 1, **Then** comportamento permanece equivalente ao anterior.
2. **Given** documentação interativa em execução, **When** consultada, **Then** operações de sugestão de trocas e ranking estão descritas e testáveis.
3. **Given** troca aceita após consulta de ranking, **When** ranking é consultado novamente, **Then** métricas refletem o estado atualizado (ex.: trocas aceitas incrementadas, coleções atualizadas refletidas no percentual).

---

### Edge Cases

- O que acontece se entre a consulta de sugestão e a criação da proposta a coleção muda? A sugestão pode ficar desatualizada; a criação de proposta aplica validações atuais de posse (comportamento existente).
- Múltiplas sugestões entre os mesmos dois usuários com pares de figurinhas diferentes? Todas as oportunidades válidas podem ser listadas; não há limite artificial além do que for razoável para demonstração didática.
- Usuário com 100% do álbum ainda aparece no ranking? Sim, com percentual 100%; sugestões para esse usuário tendem a ser vazias se não houver figurinhas que ele precise.
- Sugestão duplicada simétrica (A-B com 10/25 e B-A com 25/10)? Deve representar a mesma oportunidade; ao listar para A, perspectiva é solicitante A; ao listar para B, perspectiva é solicitante B — não é necessário duplicar a mesma oportunidade duas vezes na mesma consulta.
- Ranking com um único usuário? Retorna lista com uma entrada na posição 1.
- Trocas aceitas contam para ambos os participantes? Sim — cada usuário envolvido em troca com status ACCEPTED incrementa sua contagem de trocas aceitas em 1.
- Consulta global de sugestões (sem filtrar por usuário)? Pode ser suportada para visão administrativa/didática; consulta por usuário é o fluxo principal da interface.
- Coleções idênticas entre dois usuários sem complementaridade? Nenhuma sugestão entre eles.

## Requirements *(mandatory)*

### Functional Requirements

**Sugestão automática de trocas**

- **FR-001**: O sistema MUST analisar as coleções dos usuários cadastrados para identificar oportunidades de troca mútua.
- **FR-002**: O sistema MUST permitir consultar sugestões de troca para um usuário identificado.
- **FR-003**: Cada sugestão MUST indicar: usuário A (perspectiva do solicitante na oportunidade), usuário B (parceiro), figurinha que A pode oferecer (número) e figurinha que B pode oferecer (número, correspondente ao que A deseja receber).
- **FR-004**: O sistema MUST incluir na sugestão apenas pares em que A oferece figurinha repetida (quantidade ≥ 2, ou quantidade ≥ 1 quando regra de figurinha única desativada) que B não possui (quantidade 0 ou ausente na coleção).
- **FR-005**: O sistema MUST incluir na sugestão apenas pares em que B oferece figurinha repetida (mesmos critérios de FR-004) que A não possui.
- **FR-006**: O sistema MUST considerar troca justa nas sugestões: exatamente uma figurinha por uma figurinha, com números distintos entre a oferecida por A e a oferecida por B.
- **FR-007**: O sistema MUST NOT sugerir troca envolvendo o mesmo usuário como A e B.
- **FR-008**: Quando a regra de proteção de figurinha única estiver ativa, o sistema MUST NOT sugerir troca em que qualquer participante ofereceria figurinha com quantidade igual a 1.
- **FR-009**: O sistema MUST NOT criar propostas de troca automaticamente ao gerar ou consultar sugestões.
- **FR-010**: O sistema MUST NOT alterar coleções ao gerar ou consultar sugestões.
- **FR-011**: O sistema MUST NOT persistir sugestões como entidades de negócio equivalentes a propostas; sugestões são oportunidades calculadas sob demanda com base no estado atual das coleções.
- **FR-012**: O sistema MUST rejeitar consulta de sugestões para usuário inexistente com mensagem compreensível.

**Ranking de usuários**

- **FR-013**: O sistema MUST permitir consultar ranking de todos os usuários cadastrados.
- **FR-014**: O ranking MUST incluir, para cada usuário: identificação (nome e/ou identificador único), quantidade de figurinhas únicas na coleção, percentual de conclusão do álbum e quantidade de trocas aceitas.
- **FR-015**: O percentual de conclusão MUST ser calculado como (quantidade de figurinhas únicas possuídas ÷ tamanho total do álbum) × 100, considerando figurinha possuída quando quantidade ≥ 1.
- **FR-016**: O ranking MUST ordenar usuários por percentual de conclusão decrescente (maior percentual em melhor posição).
- **FR-017**: Em caso de empate no percentual de conclusão, o sistema MUST usar quantidade de trocas aceitas como critério secundário (maior quantidade em melhor posição).
- **FR-018**: A contagem de trocas aceitas MUST considerar propostas com status ACCEPTED em que o usuário participou como solicitante ou destinatário.
- **FR-019**: O sistema MUST NOT alterar dados de usuários, coleções ou propostas ao calcular ou consultar o ranking.
- **FR-020**: O ranking MUST refletir o estado atual do sistema no momento da consulta (cálculo sob demanda).

**Interface web e compatibilidade**

- **FR-021**: A interface web MUST exibir sugestões de troca para o usuário selecionado, com parceiro e figurinhas envolvidas claramente visíveis.
- **FR-022**: A interface web MUST permitir utilizar uma sugestão como base para iniciar criação de proposta de troca, pré-preenchendo solicitante, destinatário, figurinha oferecida e figurinha desejada conforme a sugestão.
- **FR-023**: A interface web MUST exibir o ranking de usuários com percentual de conclusão e quantidade de trocas aceitas.
- **FR-024**: A interface web MUST apresentar mensagens de erro compreensíveis quando consultas de sugestão ou ranking falharem.
- **FR-025**: O sistema MUST preservar todas as funcionalidades do MVP e da Evolução 1: cadastro, coleção, repetidas, criação de propostas, aceite, recusa e validações de negócio na efetivação.
- **FR-026**: A documentação interativa MUST incluir operações de consulta de sugestões e ranking, testáveis no navegador.
- **FR-027**: O sistema MUST apresentar mensagens de erro compreensíveis quando parâmetros de consulta forem inválidos.

### Key Entities

- **Usuário**: Inalterado em relação às fases anteriores. Colecionador com identificador único e nome; possui uma Coleção.
- **Álbum**: Inalterado. Catálogo finito de números válidos; o tamanho total do álbum é usado no cálculo do percentual de conclusão.
- **Figurinha**: Inalterado. Item do álbum identificado por número.
- **Coleção**: Inalterado em estrutura. Fonte de dados para sugestões (repetidas, ausências) e para ranking (figurinhas únicas).
- **Troca (proposta)**: Inalterado em relação à Evolução 1. Histórico de propostas ACCEPTED alimenta a métrica de trocas aceitas no ranking. Sugestões não são propostas.
- **Sugestão de troca** (conceito derivado, consultivo): Oportunidade calculada entre dois usuários. Atributos: usuário A, usuário B, figurinha oferecida por A, figurinha oferecida por B (que A receberia). Não possui ciclo de vida nem persistência obrigatória; não altera o domínio persistente.
- **Entrada de ranking** (conceito derivado, consultivo): Posição e métricas de um usuário no ranking. Atributos: posição ordinal, usuário, quantidade de figurinhas únicas, percentual de conclusão, quantidade de trocas aceitas. Calculado sob demanda.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% das sugestões retornadas em cenários de teste com coleções conhecidas respeitam benefício mútuo, troca justa (1:1, números distintos) e regra de figurinha única quando ativa.
- **SC-002**: 100% das consultas de sugestões e ranking não alteram coleções nem criam propostas automaticamente.
- **SC-003**: Participantes conseguem consultar sugestões para um usuário e visualizar ranking exclusivamente pelo navegador em menos de 2 minutos após dados de exemplo carregados.
- **SC-004**: Participantes conseguem criar proposta a partir de uma sugestão na interface web e concluir fluxo até proposta PROPOSED em menos de 3 minutos.
- **SC-005**: O ranking ordena corretamente 100% dos cenários de teste com empates intencionais no percentual de conclusão, usando trocas aceitas como desempate.
- **SC-006**: Todos os critérios de aceite definidos pelo solicitante da evolução são verificáveis por cenários de teste manuais ou automatizados.
- **SC-007**: Nenhuma funcionalidade do MVP ou da Evolução 1 deixa de funcionar após entrega desta evolução (regressão zero nos fluxos cadastro → coleção → repetidas → proposta → aceite/recusa).
- **SC-008**: Participantes do treinamento conseguem demonstrar fluxo completo incluindo sugestões e ranking em sessão didática de menos de 10 minutos.

## Assumptions

- A Evolução 2 opera sobre o mesmo álbum único, modelo de usuários e propostas da Evolução 1, sem autenticação — operações identificam usuários pelo identificador único do sistema.
- Sugestões são calculadas sob demanda a partir do estado atual das coleções; não há obrigatoriedade de armazenar histórico de sugestões nem de invalidar sugestões explicitamente — uma nova consulta reflete o estado atual.
- "Figurinha que o outro não possui" significa quantidade 0 ou ausência da entrada na coleção do parceiro.
- "Figurinha repetida" para fins de sugestão segue a mesma lógica de repetidas do MVP (quantidade > 1), exceto quando a regra de figurinha única está desativada, caso em que quantidade ≥ 1 pode ser oferecida (alinhado à Evolução 1 na criação/aceite de propostas).
- A regra opcional de proteção de figurinha única é a mesma configurável da Evolução 1; **valor padrão: desativada**, preservando compatibilidade didática.
- Troca justa nas sugestões segue a definição da Evolução 1: 1 figurinha por 1 figurinha com números distintos.
- O tamanho do álbum para cálculo de percentual é o total de figurinhas válidas configurado no sistema (mesmo N usado nas validações de número de figurinha).
- Critério terciário de desempate no ranking (ex.: quantidade de figurinhas únicas ou ordem alfabética) não é exigido; empates restantes após percentual e trocas aceitas podem ser resolvidos por ordem estável arbitrária (ex.: identificador do usuário) sem impacto funcional.
- A interface web permanece simples (listagens e formulários), sem notificações, chat ou design avançado.
- Consulta de sugestões por usuário é o fluxo principal; consulta global de todas as oportunidades entre pares pode ser oferecida como complemento didático mas não é obrigatória para aceite da feature.
- Documentação interativa e execução simplificada herdadas das fases anteriores continuam válidas e são atualizadas na fase de plan.

## Out of Scope (Evolução 2)

- Chat ou mensagens entre usuários.
- Notificações automáticas sobre sugestões ou mudanças no ranking.
- Algoritmos complexos de recomendação, scoring ponderado de figurinhas ou machine learning.
- Interface gráfica avançada, gamificação elaborada (badges, níveis, pontos) ou aplicativo mobile.
- Integração com sistemas ou APIs externas.
- Persistência e ciclo de vida de sugestões (aceitar/descartar sugestão como entidade).
- Criação automática de propostas a partir de sugestões.
- Alteração de coleções ou ranking como efeito colateral de consultas.
- Autenticação, autorização ou perfis de acesso.
- Múltiplos álbuns ou edições de Copa simultâneas.
- Troca de múltiplas figurinhas em uma única sugestão (N por M).

## Dependencies

- **MVP (001-sticker-trade-mvp)**: Cadastro de usuários, coleções, repetidas, criação de propostas e infraestrutura de demonstração devem estar entregues e estáveis.
- **Evolução 1 (002-trade-accept-reject)**: Aceite e recusa de propostas, validações de negócio na efetivação, status ACCEPTED/REJECTED e atualização de coleções no aceite devem estar entregues e estáveis — necessários para métrica de trocas aceitas e regras de figurinha única/troca justa.
- **Constituição do projeto**: Regras de domínio centralizadas, testes de domínio, demonstrabilidade visual (Princípios IV, VII e VIII) e evolução incremental (Princípio III) aplicam-se a esta evolução.
