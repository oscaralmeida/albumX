# Feature Specification: MVP de Troca de Figurinhas da Copa

**Feature Branch**: `001-sticker-trade-mvp`

**Created**: 2026-06-26

**Status**: Draft

**Input**: User description: "Crie a especificação funcional do MVP para um sistema de troca de figurinhas da Copa. Objetivo: permitir que usuários cadastrem suas coleções, identifiquem figurinhas repetidas e proponham trocas com outros usuários. Inclui interface web básica, documentação interativa da API e execução simplificada (preferencialmente Docker)."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Cadastro de colecionador (Priority: P1)

Como colecionador de figurinhas da Copa, quero me cadastrar no sistema com um nome identificável, para que outros colecionadores possam me reconhecer e eu possa registrar minha coleção.

**Why this priority**: O cadastro de usuários é a base de todas as demais funcionalidades. Sem usuários identificados, não há coleções nem trocas.

**Independent Test**: Pode ser testado cadastrando um usuário e verificando que o sistema retorna um identificador único e persiste o nome informado.

**Acceptance Scenarios**:

1. **Given** que não existe usuário cadastrado com o nome informado, **When** um colecionador solicita cadastro com um nome válido, **Then** o sistema cria o usuário e retorna um identificador único.
2. **Given** que um usuário já foi cadastrado, **When** outro colecionador solicita cadastro com nome válido, **Then** o sistema cria um novo usuário com identificador distinto do anterior.
3. **Given** que um colecionador solicita cadastro, **When** o nome informado está vazio ou contém apenas espaços, **Then** o sistema rejeita o cadastro e informa que o nome é obrigatório.

---

### User Story 2 - Registro e visualização da coleção (Priority: P2)

Como colecionador cadastrado, quero registrar quais figurinhas possuo, em que quantidade, e visualizar minha coleção completa, para que o sistema conheça meu inventário e eu possa acompanhar o que já tenho.

**Why this priority**: A coleção é o dado central do domínio. Sem ela, não há como consultar repetidas nem oferecer figurinhas em trocas.

**Independent Test**: Pode ser testado cadastrando um usuário, adicionando figurinhas à sua coleção (incluindo múltiplas unidades da mesma figurinha), consultando a coleção e verificando que as quantidades exibidas correspondem ao registrado.

**Acceptance Scenarios**:

1. **Given** um usuário cadastrado e uma figurinha identificada por número, **When** o usuário registra a posse de uma unidade dessa figurinha, **Then** a figurinha passa a constar na coleção do usuário com quantidade 1.
2. **Given** um usuário que já possui uma figurinha na coleção, **When** o usuário registra outra unidade da mesma figurinha, **Then** a quantidade dessa figurinha na coleção é incrementada.
3. **Given** um usuário com figurinhas registradas, **When** consulta sua coleção, **Then** o sistema exibe todas as figurinhas possuídas com respectivo número e quantidade.
4. **Given** um usuário cadastrado sem figurinhas, **When** consulta sua coleção, **Then** o sistema retorna coleção vazia.
5. **Given** um usuário cadastrado, **When** tenta registrar figurinha com número inválido (vazio, negativo ou fora do álbum), **Then** o sistema rejeita o registro e informa o motivo.
6. **Given** um identificador de usuário inexistente, **When** alguém tenta registrar ou consultar figurinhas nessa coleção, **Then** o sistema rejeita a operação.

---

### User Story 3 - Consulta de figurinhas repetidas (Priority: P3)

Como colecionador, quero consultar quais figurinhas da minha coleção estão repetidas, para saber quais posso oferecer em trocas.

**Why this priority**: Identificar repetidas transforma a coleção em valor trocável e prepara o fluxo de propostas de troca.

**Independent Test**: Pode ser testado montando uma coleção com figurinhas em quantidades variadas e verificando que apenas as com quantidade maior que 1 aparecem na listagem de repetidas.

**Acceptance Scenarios**:

1. **Given** um usuário com figurinha X em quantidade 3 e figurinha Y em quantidade 1, **When** consulta suas repetidas, **Then** o sistema retorna apenas a figurinha X com quantidade 3.
2. **Given** um usuário cuja coleção não possui nenhuma figurinha com quantidade maior que 1, **When** consulta suas repetidas, **Then** o sistema retorna lista vazia.
3. **Given** um usuário com múltiplas figurinhas repetidas, **When** consulta suas repetidas, **Then** o sistema retorna todas as figurinhas cuja quantidade é maior que 1, com o respectivo número e quantidade.

---

### User Story 4 - Proposta de troca entre colecionadores (Priority: P4)

Como colecionador, quero propor uma troca a outro usuário indicando qual figurinha ofereço e qual desejo receber, para iniciar uma negociação de completar o álbum.

**Why this priority**: A proposta de troca é o objetivo final do MVP — conectar colecionadores com base nas coleções registradas.

**Independent Test**: Pode ser testado com dois usuários cadastrados, coleções preenchidas e criação de proposta válida; também testando rejeição quando o solicitante não possui a figurinha oferecida.

**Acceptance Scenarios**:

1. **Given** usuário A possui figurinha 10 e usuário B está cadastrado, **When** A propõe troca oferecendo figurinha 10 e solicitando figurinha 25 a B, **Then** o sistema registra a proposta com solicitante A, destinatário B, figurinha oferecida 10 e figurinha desejada 25.
2. **Given** usuário A não possui figurinha 10 em sua coleção, **When** A tenta propor troca oferecendo figurinha 10, **Then** o sistema rejeita a proposta e informa que o solicitante não possui a figurinha oferecida.
3. **Given** usuário A possui figurinha 10 em quantidade 1, **When** A propõe troca oferecendo figurinha 10, **Then** o sistema aceita a proposta (no MVP não há restrição para figurinha única).
4. **Given** solicitante ou destinatário inexistente, **When** alguém tenta criar proposta de troca, **Then** o sistema rejeita a operação.
5. **Given** uma proposta registrada com sucesso, **When** consultada, **Then** permanece registrada sem alteração nas coleções dos envolvidos (MVP não efetiva troca).

---

### User Story 5 - Interação via interface web didática (Priority: P5)

Como participante de treinamento, quero interagir com todas as funcionalidades do MVP por meio de uma interface web simples no navegador, para demonstrar e exercitar o sistema sem ferramentas externas.

**Why this priority**: A demonstrabilidade visual é requisito pedagógico do projeto; a interface web torna o MVP tangível para participantes do treinamento.

**Independent Test**: Pode ser testado acessando a aplicação no navegador e executando, via formulários e páginas simples, cadastro de usuário, registro de figurinhas, visualização de coleção, consulta de repetidas e criação de proposta de troca.

**Acceptance Scenarios**:

1. **Given** a aplicação em execução, **When** o usuário acessa a interface web, **Then** consegue cadastrar um colecionador por meio de formulário.
2. **Given** um usuário cadastrado, **When** o usuário acessa a funcionalidade de coleção na interface web, **Then** consegue adicionar figurinhas e visualizar a coleção completa desse usuário.
3. **Given** um usuário com figurinhas na coleção, **When** acessa a visualização de repetidas na interface web, **Then** vê apenas figurinhas com quantidade maior que 1.
4. **Given** dois usuários cadastrados com coleções válidas, **When** preenche o formulário de proposta de troca na interface web, **Then** a proposta é criada e confirmada ao usuário, ou rejeitada com mensagem clara em caso de violação de regra.
5. **Given** qualquer fluxo principal do MVP, **When** executado pela interface web, **Then** não é necessário usar ferramentas externas (ex.: cliente HTTP dedicado) para completar a demonstração.

---

### User Story 6 - Documentação interativa e execução simplificada (Priority: P6)

Como desenvolvedor ou instrutor de treinamento, quero subir a aplicação com um único comando e consultar/testar os endpoints por documentação interativa no navegador, para validar o sistema rapidamente e apoiar o ensino.

**Why this priority**: Reduz fricção na demonstração e no aprendizado; permite explorar a API sem montar ambiente manualmente.

**Independent Test**: Pode ser testado executando o comando único de inicialização, acessando a documentação interativa no navegador e invocando cada endpoint principal com sucesso.

**Acceptance Scenarios**:

1. **Given** o ambiente com os pré-requisitos mínimos instalados, **When** o operador executa o comando único documentado para subir a aplicação, **Then** o sistema fica disponível para acesso via navegador sem passos adicionais de configuração manual.
2. **Given** a aplicação em execução, **When** o usuário acessa a documentação interativa da API no navegador, **Then** todos os endpoints do MVP estão descritos e podem ser testados diretamente na interface.
3. **Given** um endpoint de cadastro de usuário na documentação interativa, **When** o usuário envia uma requisição válida de teste, **Then** recebe resposta coerente com o comportamento esperado do sistema.

---

### Edge Cases

- O que acontece quando o usuário tenta registrar figurinha com número duplicado na mesma operação em lote? O sistema deve consolidar em uma única entrada com quantidade somada.
- Como o sistema trata consulta de repetidas para usuário sem nenhuma figurinha na coleção? Retorna lista vazia.
- O que acontece se o solicitante oferece figurinha que possui em quantidade 0 (não consta na coleção)? A proposta é rejeitada.
- O solicitante pode propor troca para si mesmo? No MVP, a proposta deve ser rejeitada (solicitante e destinatário devem ser usuários distintos).
- Figurinha desejada pode ser igual à oferecida? No MVP, é permitido registrar a proposta (sem validação de justiça ou utilidade).
- O destinatário precisa possuir a figurinha desejada? No MVP, não há validação — apenas a posse da figurinha oferecida pelo solicitante é verificada.
- O que acontece quando a interface web recebe dados inválidos de formulário? O sistema exibe mensagem de erro compreensível sem quebrar a página.
- O que acontece se a aplicação é acessada antes de estar totalmente inicializada? O operador recebe indicação clara de indisponibilidade temporária ou aguarda até o serviço responder.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: O sistema MUST permitir cadastrar usuários colecionadores informando um nome.
- **FR-002**: O sistema MUST atribuir um identificador único e imutável a cada usuário cadastrado.
- **FR-003**: O sistema MUST rejeitar cadastro de usuário sem nome válido (não vazio após normalização de espaços).
- **FR-004**: O sistema MUST permitir registrar figurinhas na coleção de um usuário identificado por número de figurinha.
- **FR-005**: O sistema MUST suportar múltiplas unidades da mesma figurinha na coleção de um usuário, representadas por quantidade.
- **FR-006**: O sistema MUST validar que o número da figurinha pertence ao álbum da Copa configurado para o MVP (conjunto finito de números válidos).
- **FR-007**: O sistema MUST permitir consultar a coleção completa de figurinhas de um usuário, exibindo número e quantidade de cada figurinha possuída.
- **FR-008**: O sistema MUST permitir consultar as figurinhas repetidas de um usuário — aquelas cuja quantidade na coleção é maior que 1.
- **FR-009**: O sistema MUST retornar, para cada figurinha repetida, o número da figurinha e a quantidade total na coleção.
- **FR-010**: O sistema MUST permitir que um usuário crie proposta de troca indicando: usuário solicitante, usuário destinatário, figurinha oferecida e figurinha desejada.
- **FR-011**: O sistema MUST rejeitar proposta de troca quando o solicitante não possui a figurinha oferecida em sua coleção (quantidade menor que 1).
- **FR-012**: O sistema MUST rejeitar proposta de troca quando solicitante e destinatário são o mesmo usuário.
- **FR-013**: O sistema MUST persistir propostas de troca registradas sem alterar as coleções dos usuários envolvidos.
- **FR-014**: O sistema MUST rejeitar operações que referenciem usuários inexistentes.
- **FR-015**: O sistema MUST modelar explicitamente os conceitos de domínio: Usuário, Álbum, Figurinha, Coleção e Troca (proposta).
- **FR-016**: O sistema MUST oferecer interface web básica e funcional que permita: cadastrar usuários, registrar figurinhas na coleção, visualizar coleção, visualizar figurinhas repetidas e criar proposta de troca.
- **FR-017**: A interface web MUST ser simples e suficiente para demonstrar todas as funcionalidades do MVP em contexto de treinamento, sem exigir sofisticação visual.
- **FR-018**: O sistema MUST expor documentação automática e interativa de seus endpoints, acessível via navegador, permitindo testar todos os endpoints do MVP diretamente na interface de documentação.
- **FR-019**: O sistema MUST ser executável de forma simples, de preferência com um único comando que suba toda a aplicação (incluindo interface web e documentação interativa).
- **FR-020**: O sistema MUST apresentar mensagens de erro compreensíveis na interface web e na documentação interativa quando operações violarem regras de negócio ou dados forem inválidos.

### Key Entities

- **Usuário**: Colecionador do sistema. Atributos: identificador único, nome. Relacionamento: possui exatamente uma Coleção.
- **Álbum**: Conjunto finito de figurinhas da Copa que o usuário deseja completar. No MVP, representa o catálogo oficial de números válidos de figurinhas (ex.: 1 a N). Não é personalizável por usuário.
- **Figurinha**: Item individual do álbum, identificado por número. Atributos: número (identificador dentro do álbum).
- **Coleção**: Conjunto de figurinhas pertencentes a um usuário. Cada entrada associa número da figurinha à quantidade possuída (inteiro não negativo; zero equivale a não possuir).
- **Troca (proposta)**: Registro de intenção de troca entre dois usuários. Atributos: identificador da proposta, usuário solicitante, usuário destinatário, figurinha oferecida (número), figurinha desejada (número), data/hora de criação. No MVP, status fixo de "proposta registrada" — sem aceite, recusa ou efetivação.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Um colecionador consegue cadastrar-se e registrar ao menos 10 figurinhas na sua coleção em uma única sessão de uso, sem etapas desnecessárias além das capacidades do MVP.
- **SC-002**: 100% das tentativas de proposta de troca oferecendo figurinha que o solicitante não possui são rejeitadas pelo sistema com mensagem compreensível.
- **SC-003**: A consulta de figurinhas repetidas retorna resultado correto para coleções de até 700 figurinhas (tamanho típico de álbum completo) em tempo percebido como imediato pelo usuário (sem espera perceptível em condições normais de uso didático).
- **SC-004**: Todos os critérios de aceite definidos pelo solicitante da feature são verificáveis por meio de cenários de teste manuais ou automatizados.
- **SC-005**: Participantes do treinamento conseguem executar o fluxo completo (cadastro → coleção → repetidas → proposta) demonstrando o valor do MVP em menos de 5 minutos.
- **SC-006**: Participantes do treinamento conseguem executar o fluxo completo exclusivamente pelo navegador (interface web), sem depender de ferramentas externas como clientes HTTP dedicados.
- **SC-007**: Um operador consegue colocar a aplicação em execução com um único comando documentado, em ambiente de treinamento padrão, em menos de 3 minutos (excluindo download inicial de dependências).
- **SC-008**: Todos os endpoints do MVP podem ser invocados com sucesso pela documentação interativa acessível no navegador.

## Assumptions

- O MVP opera com um único álbum padrão da Copa, com conjunto fixo e conhecido de números de figurinhas válidos (assumido como numerados sequencialmente de 1 a N, onde N é configurável mas único para todo o sistema; valor padrão razoável: 670 figurinhas, típico de álbum oficial, ajustável na fase de planejamento).
- Não há autenticação, login ou verificação de identidade — qualquer operação identifica usuários apenas pelo identificador único do sistema.
- A interface web é obrigatória para demonstração didática, mas não precisa ser sofisticada — formulários e listagens simples são suficientes.
- A documentação interativa da API (ex.: Swagger/OpenAPI na fase de planejamento) complementa a interface web; ambas devem estar disponíveis quando a aplicação estiver em execução.
- A execução simplificada via container (ex.: Docker na fase de planejamento) é a abordagem preferida para o treinamento, mas o requisito funcional é "um único comando" — a tecnologia exata será definida no plan.
- Propostas de troca são apenas registros informativos; nenhuma notificação ao destinatário é exigida no MVP.
- Não é necessário validar se a troca é justa, se o destinatário possui a figurinha desejada, nem impedir oferta de figurinha única — conforme escopo explícito do MVP e evolução prevista na constituição do projeto.
- Nomes de usuário não precisam ser únicos no MVP, desde que os identificadores sejam únicos.
- A constituição do projeto prevê "troca justa" e aceite/recusa como regras de evolução futura (Evolução 1), não aplicáveis a esta fase.

## Out of Scope (MVP)

- Aceitar ou recusar proposta de troca.
- Efetivar troca (atualizar coleções após acordo).
- Validação de troca justa (quantidade ou valor equivalente entre figurinhas oferecidas e desejadas).
- Sugestão automática de trocas compatíveis.
- Ranking ou gamificação de colecionadores.
- Interface gráfica complexa, design elaborado ou aplicativo mobile dedicado.
- Autenticação, autorização ou perfis de acesso.
- Impedir proposta oferecendo figurinha com quantidade 1 (reservado para evolução futura, se desejado).
- Notificações ao destinatário sobre novas propostas.
- Histórico, listagem ou cancelamento de propostas de troca.
- Múltiplos álbuns ou edições de Copa simultâneas.
