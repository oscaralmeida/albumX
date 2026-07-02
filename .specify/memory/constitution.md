<!--
Sync Impact Report
==================
Version change: (template) → 1.0.0
Modified principles: N/A — ratificação inicial
Added sections:
  - Core Principles (8 princípios)
  - Restrições Técnicas
  - Fluxo de Desenvolvimento (Spec Kit)
  - Roadmap do Produto
  - Governance
Removed sections: N/A
Templates requiring updates:
  - .specify/templates/plan-template.md ✅ updated (Constitution Check)
  - .specify/templates/spec-template.md ✅ updated (nota de demonstrabilidade)
  - .specify/templates/tasks-template.md ✅ updated (testes de domínio obrigatórios)
  - .cursor/rules/specify-rules.mdc ⚠ pending (atualizado via /speckit.agent-context.update após plan)
Follow-up TODOs: Nenhum
-->

# AlbumX Constitution

Constituição do projeto **AlbumX** — sistema didático de troca de figurinhas da Copa,
usado como exemplo em treinamento de **Spec-Driven Development** com **Spec Kit**.

O objetivo do projeto é demonstrar, de forma clara e reproduzível, como uma ideia de
sistema evolui a partir de especificações, planejamento técnico, divisão em tarefas e
implementação orientada por IA.

## Core Principles

### I. Simplicidade Didática

O sistema MUST permanecer simples, legível e fácil de explicar em contexto de
treinamento técnico.

- Código e documentação MUST ser compreensível por participantes sem conhecimento
  prévio do projeto.
- Complexidade adicional (frameworks, padrões, infraestrutura) MUST ser justificada
  explicitamente quando introduzida; YAGNI é regra padrão.
- Cada artefato (spec, plan, tasks, código) MUST priorizar clareza sobre sofisticação.

**Rationale**: O valor principal do projeto é pedagógico; excesso de complexidade
compromete o aprendizado do fluxo Spec Kit.

### II. Domínio Explícito

O domínio MUST ser modelado com conceitos claros e estáveis: **Usuário**, **Álbum**,
**Figurinha**, **Coleção** e **Troca**.

- Entidades e serviços MUST usar nomenclatura alinhada ao vocabulário do domínio.
- Regras de negócio MUST ser documentadas na especificação antes da implementação.
- O modelo de domínio MUST ser a referência conceitual para specs, planos e código.

**Rationale**: Um domínio bem nomeado reduz ambiguidade entre especificação e código
e facilita evoluções incrementais.

### III. Spec-First e Desenvolvimento Incremental

Cada evolução MUST seguir a sequência: **especificar → planejar → dividir em tarefas →
implementar**. Nenhuma funcionalidade fora do escopo da fase atual MUST ser
implementada.

- O MVP MUST entregar valor demonstrável com o menor conjunto de capacidades possível.
- Evoluções subsequentes MUST ser especificadas em features separadas antes de
  planejamento e código.
- Escopo MUST ser delimitado por fase (MVP, Evolução 1, Evolução 2).

**Rationale**: O fluxo Spec Kit é o objeto de demonstração do treinamento; pular etapas
ou misturar fases invalida o exemplo didático.

### IV. Regras de Negócio Centralizadas

Regras de negócio MUST residir no **domínio** (entidades e serviços de domínio), não
em controllers, DTOs ou camada de persistência.

- Lógica duplicada entre camadas é PROIBIDA.
- Validações de negócio MUST ser testáveis sem infraestrutura (sem banco, sem HTTP).
- A camada de aplicação orquestra casos de uso; a infraestrutura adapta tecnologia.

**Rationale**: Centralização garante consistência, facilita testes e evita divergência
entre regras documentadas e implementadas.

### V. Arquitetura em Camadas Simples

A implementação MUST seguir arquitetura em três camadas: **domínio**, **aplicação** e
**infraestrutura**.

- Dependências MUST fluir em uma direção: infraestrutura → aplicação → domínio.
- O domínio MUST NOT depender de frameworks, banco de dados ou detalhes de transporte.
- Portas (interfaces de repositório) no domínio; adaptadores na infraestrutura.

**Rationale**: Separação clara permite trocar detalhes técnicos sem reescrever regras
de negócio — adequado ao treinamento e à evolução do sistema.

### VI. Compatibilidade e Evolução Segura

Cada evolução MUST preservar compatibilidade com funcionalidades já existentes.

- Contratos públicos (API, modelos expostos) MUST evitar breaking changes sem
  documentação e migração explícitas.
- Estados e enums reservados para fases futuras (ex.: `ACCEPTED`, `REJECTED`) MAY ser
  definidos antecipadamente, mas MUST NOT ser ativados fora do escopo da fase.
- Regressões em comportamentos já entregues MUST ser corrigidas antes de avançar fase.

**Rationale**: O roadmap incremental só funciona se cada entrega for estável para a
próxima iteração.

### VII. Testes de Domínio (NON-NEGOTIABLE)

O projeto MUST conter testes unitários que validem os principais comportamentos das
regras de negócio no domínio.

- Cada regra de negócio material descrita na spec SHOULD ter pelo menos um teste
  unitário correspondente.
- Testes de domínio MUST executar sem Spring Context completo quando possível.
- Testes MUST falhar antes da implementação quando aplicável (red-green-refactor).

**Rationale**: Testes no domínio provam que as regras documentadas foram implementadas
corretamente e servem como documentação executável no treinamento.

### VIII. Demonstrabilidade Visual

O sistema MUST ser demonstrável em ambiente visual (navegador), sem depender de
ferramentas como Postman para validar fluxos principais.

- Cada fase MUST incluir meio de demonstração acessível via browser (UI simples,
  página de documentação interativa ou equivalente).
- O quickstart da feature MUST descrever como reproduzir o fluxo visualmente.
- APIs REST MAY existir, mas MUST NOT ser o único canal de demonstração ao usuário final
  do treinamento.

**Rationale**: Participantes do treinamento precisam ver o sistema funcionando de forma
tangível; interface visual reduz fricção didática.

## Restrições Técnicas

- **Linguagem e stack**: Java 21 LTS com Spring Boot 3 para o backend; escolhas
  adicionais MUST ser documentadas no plan da feature.
- **Persistência**: adequada ao ambiente didático (ex.: H2 em memória no MVP); portas
  de repositório MUST permitir troca futura sem alterar domínio.
- **Nomenclatura**: classes, métodos, entidades e serviços MUST ter nomes claros em
  português ou inglês consistente com o código existente — sem abreviações obscuras.
- **Escopo por fase**: MUST NOT adicionar autenticação, microserviços, mensageria ou
  outras complexidades não especificadas na fase atual.
- **Documentação viva**: `spec.md`, `plan.md`, `tasks.md` e `quickstart.md` MUST
  refletir o estado real da feature em desenvolvimento.

## Fluxo de Desenvolvimento (Spec Kit)

Todas as features MUST seguir o workflow Spec Kit nesta ordem:

1. **Constituição** (`/speckit.constitution`) — princípios e governança (este documento).
2. **Especificação** (`/speckit.specify`) — histórias de usuário, requisitos e critérios
   de sucesso.
3. **Planejamento** (`/speckit.plan`) — decisões técnicas, modelo de dados, contratos.
4. **Tarefas** (`/speckit.tasks`) — divisão ordenada e rastreável para implementação.
5. **Implementação** (`/speckit.implement`) — execução das tarefas com validação.

**Quality gates obrigatórios:**

| Gate | Momento | Critério |
|------|---------|----------|
| Constitution Check | Antes e após design no plan | Plano MUST listar conformidade com cada princípio aplicável |
| Spec completa | Antes do plan | Requisitos e cenários de aceite definidos |
| Escopo da fase | Antes de implementar | Nenhum item de evolução futura implementado |
| Testes de domínio | Antes de concluir feature | Regras principais cobertas por testes unitários |
| Demonstração | Antes de concluir feature | Fluxo reproduzível via browser conforme quickstart |

## Roadmap do Produto

### MVP (fase inicial)

Escopo MUST limitar-se a:

- Cadastro de usuários.
- Registro da coleção de figurinhas por usuário.
- Identificação de figurinhas repetidas.
- Proposta de troca entre usuários.

### Evolução 1

Escopo adicional (após MVP estável e especificado):

- Aceitar ou recusar uma troca.
- Validar regras de negócio relacionadas à troca (propriedade, justiça, quantidades).

### Evolução 2

Escopo adicional (após Evolução 1 estável e especificada):

- Sugestão automática de trocas.
- Ranking de usuários.

Itens de evoluções futuras MUST NOT aparecer em specs, planos ou tarefas da fase
corrente, exceto como extensibilidade documentada (ex.: enums reservados).

## Governance

Esta constituição é a autoridade máxima para decisões de escopo, arquitetura e
qualidade no projeto AlbumX. Em caso de conflito entre código, documentação de
feature e esta constituição, a constituição prevalece até ser formalmente emendada.

**Procedimento de emenda:**

1. Propor alteração com justificativa e impacto nas features em andamento.
2. Atualizar `.specify/memory/constitution.md` com nova versão semântica.
3. Propagar mudanças aos templates em `.specify/templates/` quando aplicável.
4. Registrar alteração no Sync Impact Report (comentário HTML no topo do arquivo).

**Política de versionamento da constituição:**

- **MAJOR**: remoção ou redefinição incompatível de princípio.
- **MINOR**: novo princípio, seção ou expansão material de diretriz.
- **PATCH**: clarificações, correções de texto, refinamentos sem mudança semântica.

**Revisão de conformidade:**

- Todo `plan.md` MUST incluir seção **Constitution Check** com gates explícitos.
- Toda implementação MUST ser revisada contra o escopo da spec da fase.
- Violações MUST ser documentadas em **Complexity Tracking** no plan ou corrigidas
  antes do merge.

**Orientação em tempo de desenvolvimento:** consultar o `plan.md` da feature corrente
em `specs/` e o `README.md` na raiz do repositório para contexto de treinamento e
prompts Spec Kit.

**Version**: 1.0.0 | **Ratified**: 2026-06-26 | **Last Amended**: 2026-06-26
