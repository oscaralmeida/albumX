# Sistema de Troca de Figurinhas da Copa — Prompts para Spec Kit

Este repositório reúne prompts prontos para uso em um treinamento de **Spec-Driven Development (SDD)** com **Spec Kit**, utilizando como exemplo um sistema simples de troca de figurinhas da Copa.

A ideia é conduzir o desenvolvimento de forma incremental, passando pelas etapas:

- **Constituição do projeto** (`/speckit.constitution`)
- **Especificação funcional** (`/speckit.specify`)
- **Planejamento técnico** (`/speckit.plan`)
- **Divisão em tarefas** (`/speckit.tasks`)
- **Implementação** (`/speckit.implement`)

O projeto está organizado em três fases:

1. **MVP**
2. **Evolução 1**
3. **Evolução 2**

---

## Visão geral do projeto

### Tema

**Sistema de troca de figurinhas da Copa**

### Explicação do projeto

O projeto tem como objetivo simular o processo de colecionar e trocar figurinhas da Copa do Mundo entre usuários.

Cada usuário possui um álbum e busca completá-lo por meio da gestão de sua coleção, identificação de figurinhas repetidas e realização de trocas com outros colecionadores.

Esse cenário é simples, conhecido e didático, mas possui regras de negócio suficientes para demonstrar bem o uso do Spec Kit em um fluxo incremental.

### Domínio

O sistema é baseado nos seguintes conceitos principais:

- **Álbum**: conjunto de figurinhas que o usuário deseja completar.
- **Figurinhas**: itens individuais que compõem o álbum.
- **Repetidas**: figurinhas que o usuário possui em quantidade maior que o necessário.
- **Troca**: mecanismo que permite negociar figurinhas entre usuários.

### Regras de negócio principais

- Cada usuário possui uma coleção de figurinhas.
- Um usuário pode ter figurinhas repetidas.
- A troca precisa ser justa.
- O usuário não pode trocar uma figurinha que não possui.
- Opcionalmente, o sistema pode impedir que o usuário troque a única unidade de uma figurinha.

---

## Planejamento incremental

### MVP

O MVP contempla as funcionalidades essenciais para viabilizar o uso básico do sistema:

- Cadastro de usuários.
- Registro da coleção de figurinhas por usuário.
- Controle de figurinhas repetidas.
- Possibilidade de propor trocas entre usuários.

### Evolução 1

A primeira evolução adiciona controle e consistência ao fluxo de troca:

- Aceitar ou recusar trocas.
- Validar regras de negócio durante as trocas.

### Evolução 2

A segunda evolução adiciona inteligência e engajamento ao sistema:

- Sugestão automática de trocas.
- Ranking de usuários.

---

# MVP — Sistema de Troca de Figurinhas da Copa

## Iniciando o Spec

```text
specify init albumX
cd albumX
```

---

## 1. Prompt — Constituição do Projeto

```text
/speckit.constitution

Crie a constituição do projeto para um sistema simples de troca de figurinhas da Copa, que será usado como exemplo em um treinamento de Spec-Driven Development com Spec Kit.

O objetivo do projeto é demonstrar, de forma didática, como uma ideia de sistema pode evoluir a partir de especificações claras, planejamento técnico, divisão em tarefas e implementação orientada por IA.

Contexto do sistema:
O sistema permite que usuários colecionem figurinhas de um álbum da Copa, registrem suas figurinhas, identifiquem figurinhas repetidas e proponham trocas com outros usuários.

Princípios do projeto:
- O sistema deve ser simples, didático e fácil de entender.
- O domínio deve ser modelado de forma clara, usando conceitos como usuário, álbum, figurinha, coleção e troca.
- O desenvolvimento deve priorizar regras de negócio explícitas e bem documentadas.
- A implementação deve favorecer código limpo, organizado e fácil de evoluir.
- Cada evolução do sistema deve preservar compatibilidade com as funcionalidades já existentes.
- O sistema deve ser construído de forma incremental, começando por um MVP e depois evoluindo com novas capacidades.
- As regras de negócio devem ser implementadas de forma centralizada, evitando lógica duplicada.
- O projeto deve conter testes para validar os principais comportamentos do domínio.
- O código deve ser legível o suficiente para ser usado em treinamento técnico.
- A arquitetura deve ser simples, sem excesso de complexidade desnecessária.

Diretrizes técnicas:
- Utilizar uma arquitetura em camadas simples, separando domínio, aplicação e infraestrutura.
- Evitar acoplamento direto entre regras de negócio e detalhes técnicos.
- Criar nomes claros para classes, métodos, entidades e serviços.
- Priorizar testes unitários para regras de negócio.
- Manter o domínio como principal fonte das regras do sistema.
- Não adicionar funcionalidades fora do escopo definido para cada fase.
- Cada nova evolução deve ser especificada antes de ser planejada e implementada.

Escopo inicial:
O MVP deve contemplar:
- cadastro de usuários;
- registro da coleção de figurinhas por usuário;
- identificação de figurinhas repetidas;
- proposta de troca entre usuários.

Evoluções previstas:
Evolução 1:
- aceitar ou recusar uma troca;
- validar regras de negócio relacionadas à troca.

Evolução 2:
- sugestão automática de trocas;
- ranking de usuários.

Gere uma constituição clara, objetiva e organizada para orientar todas as próximas etapas do desenvolvimento com Spec Kit.
```

---

## 2. Prompt — Specify do MVP

```text
/speckit.specify

Crie a especificação funcional do MVP para um sistema de troca de figurinhas da Copa.

Objetivo do MVP:
Permitir que usuários cadastrem suas coleções de figurinhas, identifiquem figurinhas repetidas e proponham trocas com outros usuários.

Contexto:
Colecionadores de figurinhas da Copa querem completar seus álbuns. Para isso, precisam registrar quais figurinhas possuem, identificar quais estão repetidas e propor trocas com outros colecionadores.

Domínio principal:
- Álbum: conjunto de figurinhas da Copa que o usuário deseja completar.
- Figurinha: item individual que compõe o álbum.
- Coleção: conjunto de figurinhas pertencentes a um usuário.
- Figurinha repetida: figurinha que o usuário possui em quantidade maior que uma unidade.
- Troca: proposta feita por um usuário para trocar uma ou mais figurinhas com outro usuário.
- Usuário: colecionador que possui uma coleção de figurinhas.

Funcionalidades do MVP:
1. Cadastro de usuários
   - O sistema deve permitir cadastrar usuários colecionadores.
   - Cada usuário deve possuir um identificador único.
   - Cada usuário deve possuir nome.

2. Registro de coleção de figurinhas
   - O sistema deve permitir registrar figurinhas na coleção de um usuário.
   - Cada figurinha deve possuir um identificador ou número.
   - O usuário pode possuir mais de uma unidade da mesma figurinha.

3. Identificação de figurinhas repetidas
   - O sistema deve permitir consultar quais figurinhas de um usuário estão repetidas.
   - Uma figurinha é considerada repetida quando o usuário possui mais de uma unidade dela.

4. Proposta de troca
   - O sistema deve permitir que um usuário proponha uma troca com outro usuário.
   - A proposta deve indicar:
     - usuário solicitante;
     - usuário destinatário;
     - figurinha oferecida;
     - figurinha desejada.
   - No MVP, a troca será apenas proposta, sem fluxo de aceite ou recusa.

Regras de negócio do MVP:
- Um usuário possui uma coleção de figurinhas.
- Um usuário pode possuir figurinhas repetidas.
- Uma figurinha repetida é aquela cuja quantidade na coleção é maior que um.
- Um usuário só pode propor uma troca oferecendo uma figurinha que possui.
- A proposta de troca deve registrar a figurinha oferecida e a figurinha desejada.
- No MVP, não é necessário efetivar a troca entre as coleções.
- No MVP, não é necessário aceitar ou recusar uma troca.
- No MVP, não é necessário validar se a troca é justa.
- No MVP, não é necessário impedir troca de figurinha única, a menos que isso seja preparado para evolução futura.

Critérios de aceite:
- Deve ser possível cadastrar um usuário.
- Deve ser possível adicionar figurinhas à coleção de um usuário.
- Deve ser possível registrar mais de uma unidade da mesma figurinha para um usuário.
- Deve ser possível listar as figurinhas repetidas de um usuário.
- Deve ser possível criar uma proposta de troca entre dois usuários.
- O sistema não deve permitir propor troca oferecendo uma figurinha que o usuário não possui.

Fora do escopo do MVP:
- Aceitar ou recusar troca.
- Efetivar troca entre coleções.
- Sugestão automática de trocas.
- Ranking de usuários.
- Interface gráfica complexa.
- Autenticação de usuários.

Gere a especificação em formato claro, estruturado e adequado para orientar as próximas fases do Spec Kit.
```

---

## 3. Prompt — Plan do MVP

```text
/speckit.plan

Crie o plano técnico para implementar o MVP do sistema de troca de figurinhas da Copa, com base na especificação funcional existente.

Objetivo técnico:
Implementar uma aplicação simples, didática e evolutiva que permita cadastrar usuários, registrar coleções de figurinhas, identificar repetidas e propor trocas.

Escopo do MVP:
- cadastro de usuários;
- registro de figurinhas na coleção do usuário;
- consulta de figurinhas repetidas;
- criação de proposta de troca.

Diretrizes de arquitetura:
- Utilizar arquitetura simples em camadas.
- Separar domínio, aplicação e infraestrutura.
- Centralizar regras de negócio em serviços ou componentes de domínio.
- Evitar lógica de negócio espalhada em controllers ou endpoints.
- Facilitar evolução futura para aceitar/recusar trocas, validar regras e sugerir trocas automáticas.

Modelo de domínio sugerido:
- User
  - id
  - name

- Sticker
  - id
  - number
  - description opcional

- UserCollection
  - userId
  - stickers
  - quantidade de cada figurinha

- TradeProposal
  - id
  - requesterUserId
  - targetUserId
  - offeredStickerNumber
  - requestedStickerNumber
  - status

Status inicial da troca:
- PROPOSED

Serviços de domínio sugeridos:
- UserService
  - cadastrar usuário
  - buscar usuário

- CollectionService
  - adicionar figurinha à coleção
  - consultar coleção
  - listar figurinhas repetidas

- TradeService
  - criar proposta de troca
  - validar se o usuário possui a figurinha oferecida

Regras técnicas:
- A quantidade de figurinhas deve ser controlada por usuário.
- O sistema deve identificar repetidas com base na quantidade maior que um.
- A proposta de troca deve ser criada apenas se o usuário solicitante possuir a figurinha oferecida.
- O status inicial da proposta deve ser PROPOSED.
- Não implementar aceite, recusa ou efetivação de troca nesta fase.

Endpoints ou casos de uso esperados:
- Criar usuário.
- Adicionar figurinha à coleção de um usuário.
- Listar coleção de um usuário.
- Listar figurinhas repetidas de um usuário.
- Criar proposta de troca.
- Listar propostas de troca.

Testes esperados:
- Deve cadastrar usuário com sucesso.
- Deve adicionar figurinha à coleção.
- Deve permitir múltiplas unidades da mesma figurinha.
- Deve identificar figurinhas repetidas.
- Deve criar proposta de troca válida.
- Deve impedir proposta quando o usuário não possui a figurinha oferecida.

Gere um plano técnico objetivo, com decisões de arquitetura, modelo de dados, principais componentes, casos de uso, validações e estratégia de testes.
```

---

## 4. Prompt — Tasks do MVP

```text
/speckit.tasks

Com base na especificação e no plano técnico do MVP do sistema de troca de figurinhas da Copa, gere a lista de tarefas necessárias para implementação.

Organize as tarefas em etapas pequenas, claras e executáveis.

Escopo do MVP:
- cadastro de usuários;
- coleção de figurinhas;
- registro de figurinhas repetidas;
- proposta de troca.

Sugestão de organização das tarefas:

1. Estrutura inicial do projeto
   - Criar estrutura de pacotes/camadas.
   - Configurar dependências necessárias.
   - Criar estrutura para testes.

2. Modelo de domínio
   - Criar entidade User.
   - Criar entidade Sticker.
   - Criar estrutura UserCollection.
   - Criar entidade TradeProposal.
   - Criar enum TradeStatus com status PROPOSED.

3. Serviços de aplicação/domínio
   - Criar UserService para cadastro e consulta de usuários.
   - Criar CollectionService para gerenciar figurinhas do usuário.
   - Criar TradeService para criar propostas de troca.

4. Regras de coleção
   - Implementar adição de figurinha à coleção.
   - Implementar controle de quantidade por figurinha.
   - Implementar consulta de coleção.
   - Implementar identificação de figurinhas repetidas.

5. Regras de proposta de troca
   - Implementar criação de proposta de troca.
   - Validar se o usuário solicitante possui a figurinha oferecida.
   - Definir status inicial da troca como PROPOSED.
   - Impedir criação de proposta inválida.

6. Interface de entrada
   - Criar endpoints, comandos ou controllers para cadastro de usuários.
   - Criar endpoint para adicionar figurinha à coleção.
   - Criar endpoint para listar coleção.
   - Criar endpoint para listar repetidas.
   - Criar endpoint para propor troca.
   - Criar endpoint para listar propostas.

7. Testes
   - Criar testes para cadastro de usuário.
   - Criar testes para adição de figurinhas.
   - Criar testes para identificação de repetidas.
   - Criar testes para criação de proposta válida.
   - Criar testes para impedir proposta com figurinha inexistente na coleção do usuário.

8. Revisão final
   - Verificar se o MVP não implementa funcionalidades das evoluções futuras.
   - Garantir clareza no código.
   - Garantir que as regras de negócio estejam centralizadas.
   - Validar se o projeto está pronto para evoluir para aceite/recusa de trocas.

Gere as tarefas em formato numerado, com descrição objetiva e dependências quando necessário.
```

---

## 5. Prompt — Implement do MVP

```text
/speckit.implement

Implemente o MVP do sistema de troca de figurinhas da Copa com base nas tarefas geradas.

Escopo do MVP:
- cadastro de usuários;
- registro de coleção de figurinhas por usuário;
- identificação de figurinhas repetidas;
- criação de proposta de troca.

Restrições importantes:
- Não implementar aceite ou recusa de troca nesta fase.
- Não implementar efetivação da troca entre coleções nesta fase.
- Não implementar sugestão automática de trocas.
- Não implementar ranking de usuários.
- Manter a implementação simples, didática e fácil de explicar em treinamento.

Regras que devem ser implementadas:
- Cada usuário possui uma coleção de figurinhas.
- Um usuário pode possuir múltiplas unidades da mesma figurinha.
- Uma figurinha é repetida quando sua quantidade é maior que um.
- Uma proposta de troca deve conter usuário solicitante, usuário destinatário, figurinha oferecida e figurinha desejada.
- O usuário solicitante só pode oferecer uma figurinha que possui.
- Toda proposta criada no MVP deve iniciar com status PROPOSED.

Critérios de qualidade:
- Código limpo e organizado.
- Separação clara entre domínio, serviços e entrada da aplicação.
- Regras de negócio centralizadas.
- Testes cobrindo os principais fluxos.
- Nomes de classes, métodos e variáveis claros.
- Implementação preparada para evoluir nas próximas fases.

Implemente as tarefas na ordem definida, garantindo que cada funcionalidade esteja coberta por testes.
```

---

# Evolução 1 — Aceitar/Recusar Troca e Validar Regras

## 6. Prompt — Specify da Evolução 1

```text
/speckit.specify

Crie a especificação funcional da Evolução 1 do sistema de troca de figurinhas da Copa.

Contexto:
O MVP já permite cadastrar usuários, registrar coleções, identificar figurinhas repetidas e criar propostas de troca com status PROPOSED.

Objetivo da Evolução 1:
Permitir que o usuário destinatário aceite ou recuse uma proposta de troca, além de validar regras de negócio para garantir que a troca seja consistente e justa.

Novas funcionalidades:
1. Aceitar troca
   - O usuário destinatário deve poder aceitar uma proposta de troca recebida.
   - Ao aceitar, o sistema deve validar todas as regras necessárias.
   - Se a troca for válida, as coleções dos usuários devem ser atualizadas.
   - O status da troca deve ser alterado para ACCEPTED.

2. Recusar troca
   - O usuário destinatário deve poder recusar uma proposta de troca recebida.
   - Ao recusar, nenhuma coleção deve ser alterada.
   - O status da troca deve ser alterado para REJECTED.

3. Validação de regras de negócio
   - O usuário solicitante deve possuir a figurinha oferecida.
   - O usuário destinatário deve possuir a figurinha solicitada.
   - A troca deve ser justa.
   - Considerar troca justa como troca de uma figurinha por uma figurinha.
   - Opcionalmente, o sistema pode impedir que usuários troquem a única unidade de uma figurinha.
   - Uma troca já aceita ou recusada não pode ser alterada novamente.

Regras de negócio:
- Apenas propostas com status PROPOSED podem ser aceitas ou recusadas.
- Uma proposta aceita deve atualizar as coleções dos dois usuários.
- Uma proposta recusada não deve alterar nenhuma coleção.
- O solicitante deve perder a figurinha oferecida e receber a figurinha desejada.
- O destinatário deve perder a figurinha desejada pelo solicitante e receber a figurinha oferecida.
- O sistema não deve permitir aceitar troca se alguma das figurinhas envolvidas não estiver mais disponível.
- O sistema deve impedir alterações em propostas já finalizadas.
- Se a regra de não trocar figurinha única estiver ativa, o sistema deve validar se cada usuário possui mais de uma unidade da figurinha que irá entregar.

Critérios de aceite:
- Deve ser possível aceitar uma proposta válida.
- Ao aceitar uma proposta válida, as coleções devem ser atualizadas corretamente.
- Deve ser possível recusar uma proposta.
- Ao recusar uma proposta, as coleções não devem ser alteradas.
- Não deve ser possível aceitar proposta já aceita.
- Não deve ser possível recusar proposta já aceita.
- Não deve ser possível aceitar proposta quando o solicitante não possui mais a figurinha oferecida.
- Não deve ser possível aceitar proposta quando o destinatário não possui a figurinha solicitada.
- Se configurado, não deve ser possível trocar a única unidade de uma figurinha.

Fora do escopo desta evolução:
- Sugestão automática de trocas.
- Ranking de usuários.
- Notificações.
- Chat entre usuários.
- Interface gráfica avançada.

Gere a especificação funcional completa da Evolução 1, mantendo compatibilidade com o MVP.
```

---

## 7. Prompt — Plan da Evolução 1

```text
/speckit.plan

Crie o plano técnico para implementar a Evolução 1 do sistema de troca de figurinhas da Copa.

Contexto:
O MVP já possui usuários, coleções de figurinhas, identificação de repetidas e propostas de troca com status PROPOSED.

Objetivo técnico:
Adicionar o fluxo de aceite e recusa de propostas de troca, aplicando validações de negócio e atualizando as coleções dos usuários quando a troca for aceita.

Alterações no domínio:
- Expandir o enum TradeStatus para incluir:
  - PROPOSED
  - ACCEPTED
  - REJECTED

Novos comportamentos:
- Aceitar proposta de troca.
- Recusar proposta de troca.
- Validar disponibilidade das figurinhas no momento do aceite.
- Atualizar coleções dos dois usuários quando a troca for aceita.
- Bloquear alterações em propostas finalizadas.
- Opcionalmente, impedir troca da única unidade de uma figurinha.

Serviços impactados:
- TradeService
  - adicionar método para aceitar troca.
  - adicionar método para recusar troca.
  - adicionar validação de status.
  - adicionar validação de posse das figurinhas.
  - adicionar validação de troca justa.
  - adicionar integração com CollectionService para atualizar coleções.

- CollectionService
  - adicionar método para remover figurinha da coleção.
  - adicionar método para consultar quantidade de uma figurinha.
  - adicionar método para validar se usuário possui figurinha.
  - adicionar método para validar se usuário possui figurinha repetida, caso a regra de não trocar figurinha única seja aplicada.

Regras técnicas:
- A troca deve ser transacional do ponto de vista lógico.
- Se qualquer validação falhar, nenhuma coleção deve ser alterada.
- A atualização das coleções deve ocorrer apenas após todas as validações.
- O status da troca deve mudar para ACCEPTED somente após sucesso na atualização das coleções.
- O status da troca deve mudar para REJECTED quando o destinatário recusar.
- Uma troca finalizada não pode voltar para PROPOSED.

Casos de uso:
- Aceitar troca.
- Recusar troca.
- Consultar status da troca.
- Validar proposta antes do aceite.

Testes esperados:
- Deve aceitar troca válida.
- Deve atualizar corretamente a coleção do solicitante.
- Deve atualizar corretamente a coleção do destinatário.
- Deve recusar troca sem alterar coleções.
- Deve impedir aceitar troca já aceita.
- Deve impedir recusar troca já aceita.
- Deve impedir aceitar troca sem figurinha disponível.
- Deve impedir troca injusta.
- Deve validar regra opcional de não trocar figurinha única.

Gere um plano técnico claro e incremental para implementar a Evolução 1 sem quebrar o comportamento do MVP.
```

---

## 8. Prompt — Tasks da Evolução 1

```text
/speckit.tasks

Com base na especificação e no plano técnico da Evolução 1, gere as tarefas necessárias para implementar aceite, recusa e validação de trocas no sistema de troca de figurinhas da Copa.

Organize as tarefas em passos pequenos e executáveis.

Escopo da Evolução 1:
- aceitar troca;
- recusar troca;
- validar regras de negócio;
- atualizar coleções quando a troca for aceita.

Tarefas esperadas:

1. Atualizar modelo de status da troca
   - Adicionar status ACCEPTED.
   - Adicionar status REJECTED.
   - Garantir que novas propostas continuem iniciando como PROPOSED.

2. Expandir CollectionService
   - Criar método para remover figurinha da coleção.
   - Criar método para consultar quantidade de uma figurinha na coleção.
   - Criar método para verificar se usuário possui determinada figurinha.
   - Criar método para verificar se usuário possui quantidade suficiente para troca.
   - Garantir que a coleção não fique com quantidade negativa.

3. Expandir TradeService
   - Criar método para aceitar proposta de troca.
   - Criar método para recusar proposta de troca.
   - Validar se a proposta está com status PROPOSED.
   - Validar se o solicitante possui a figurinha oferecida.
   - Validar se o destinatário possui a figurinha solicitada.
   - Validar se a troca é justa.
   - Aplicar regra opcional de impedir troca da única unidade, se configurada.
   - Atualizar coleções em caso de aceite.
   - Atualizar status da proposta para ACCEPTED ou REJECTED.

4. Criar endpoints ou comandos
   - Criar endpoint para aceitar troca.
   - Criar endpoint para recusar troca.
   - Criar endpoint para consultar status de uma troca.

5. Testes de aceite
   - Testar aceite de troca válida.
   - Testar atualização correta da coleção do solicitante.
   - Testar atualização correta da coleção do destinatário.
   - Testar alteração do status para ACCEPTED.

6. Testes de recusa
   - Testar recusa de troca.
   - Validar que as coleções não são alteradas.
   - Testar alteração do status para REJECTED.

7. Testes de validação
   - Testar erro ao aceitar troca já aceita.
   - Testar erro ao recusar troca já aceita.
   - Testar erro ao aceitar troca já recusada.
   - Testar erro quando o solicitante não possui a figurinha oferecida.
   - Testar erro quando o destinatário não possui a figurinha solicitada.
   - Testar erro quando a troca não é justa.
   - Testar regra opcional de não trocar figurinha única.

8. Revisão final
   - Garantir que funcionalidades do MVP continuam funcionando.
   - Garantir que a troca só altera coleções após validações.
   - Garantir que não foram adicionadas funcionalidades da Evolução 2.

Gere as tarefas em formato objetivo, numerado e pronto para execução.
```

---

## 9. Prompt — Implement da Evolução 1

```text
/speckit.implement

Implemente a Evolução 1 do sistema de troca de figurinhas da Copa com base nas tarefas geradas.

Contexto:
O MVP já permite cadastrar usuários, registrar figurinhas, listar repetidas e criar propostas de troca.

Objetivo da implementação:
Adicionar o fluxo de aceitar e recusar trocas, validando regras de negócio e atualizando as coleções quando uma troca for aceita.

Funcionalidades a implementar:
- Aceitar proposta de troca.
- Recusar proposta de troca.
- Validar se a proposta ainda está com status PROPOSED.
- Validar se o solicitante possui a figurinha oferecida.
- Validar se o destinatário possui a figurinha solicitada.
- Validar se a troca é justa.
- Atualizar coleções dos dois usuários ao aceitar.
- Alterar status da proposta para ACCEPTED ou REJECTED.
- Impedir alteração de propostas já finalizadas.

Regras obrigatórias:
- Apenas propostas com status PROPOSED podem ser aceitas ou recusadas.
- Uma proposta aceita deve atualizar as coleções dos dois usuários.
- Uma proposta recusada não deve alterar coleções.
- Nenhuma coleção pode ficar com quantidade negativa.
- O status ACCEPTED só deve ser aplicado após sucesso em todas as validações.
- Se qualquer validação falhar, a troca não deve ser efetivada.

Regra opcional:
- Impedir que o usuário troque a única unidade de uma figurinha.
- Se essa regra for implementada, ela deve estar clara e centralizada.

Restrições:
- Não implementar sugestão automática de trocas nesta fase.
- Não implementar ranking de usuários nesta fase.
- Não adicionar funcionalidades fora do escopo da Evolução 1.

Critérios de qualidade:
- Manter compatibilidade com o MVP.
- Preservar clareza e simplicidade do código.
- Centralizar regras de negócio.
- Adicionar testes cobrindo fluxos de sucesso e erro.
- Garantir que a implementação continue adequada para uso didático em treinamento.

Implemente as tarefas na ordem definida e atualize ou crie testes conforme necessário.
```

---

# Evolução 2 — Sugestão Automática e Ranking

## 10. Prompt — Specify da Evolução 2

```text
/speckit.specify

Crie a especificação funcional da Evolução 2 do sistema de troca de figurinhas da Copa.

Contexto:
O sistema já possui:
- cadastro de usuários;
- registro de coleções;
- identificação de figurinhas repetidas;
- proposta de troca;
- aceite e recusa de troca;
- validação das regras de negócio;
- atualização das coleções quando uma troca é aceita.

Objetivo da Evolução 2:
Adicionar funcionalidades de inteligência e engajamento ao sistema por meio de sugestão automática de trocas e ranking de usuários.

Novas funcionalidades:

1. Sugestão automática de trocas
   - O sistema deve analisar as coleções dos usuários.
   - O sistema deve identificar oportunidades de troca entre usuários.
   - Uma sugestão deve considerar:
     - figurinhas repetidas que um usuário possui;
     - figurinhas que outro usuário ainda não possui;
     - possibilidade de troca justa entre os dois usuários.
   - A sugestão deve indicar:
     - usuário A;
     - usuário B;
     - figurinha que o usuário A pode oferecer;
     - figurinha que o usuário B pode oferecer.

2. Ranking de usuários
   - O sistema deve gerar um ranking simples dos usuários.
   - O ranking pode considerar critérios como:
     - quantidade de figurinhas únicas na coleção;
     - percentual de conclusão do álbum;
     - quantidade de trocas aceitas.
   - O ranking deve ajudar a identificar usuários mais avançados na coleção ou mais ativos em trocas.

Regras de negócio para sugestão automática:
- Uma sugestão só deve ser criada quando ambos os usuários puderem se beneficiar.
- O sistema deve sugerir trocas em que cada usuário ofereça uma figurinha repetida.
- O sistema deve evitar sugerir troca envolvendo figurinha única, caso essa regra esteja ativa.
- A sugestão não deve criar uma proposta automaticamente, apenas indicar oportunidade.
- O usuário poderá usar a sugestão como base para propor uma troca.

Regras de negócio para ranking:
- O ranking deve ser calculado com base nas informações atuais do sistema.
- O percentual de conclusão do álbum deve considerar a quantidade de figurinhas únicas que o usuário possui.
- Usuários com maior percentual de conclusão devem aparecer melhor posicionados.
- Em caso de empate, pode ser usado como critério secundário a quantidade de trocas aceitas.
- O ranking não deve alterar dados dos usuários.

Critérios de aceite:
- Deve ser possível gerar sugestões de troca entre usuários.
- As sugestões devem considerar repetidas e figurinhas ausentes.
- As sugestões não devem alterar coleções.
- As sugestões não devem criar propostas automaticamente.
- Deve ser possível consultar ranking de usuários.
- O ranking deve considerar progresso na coleção.
- O ranking pode considerar quantidade de trocas aceitas como critério adicional.
- O ranking deve ser apenas consultivo.

Fora do escopo:
- Chat entre usuários.
- Notificações automáticas.
- Algoritmos complexos de recomendação.
- Machine learning.
- Interface gráfica avançada.
- Integração externa.

Gere a especificação funcional completa da Evolução 2, mantendo compatibilidade com o MVP e com a Evolução 1.
```

---

## 11. Prompt — Plan da Evolução 2

```text
/speckit.plan

Crie o plano técnico para implementar a Evolução 2 do sistema de troca de figurinhas da Copa.

Contexto:
O sistema já possui cadastro de usuários, coleções, propostas de troca, aceite, recusa e validações de negócio.

Objetivo técnico:
Adicionar mecanismos simples para:
- sugerir automaticamente oportunidades de troca entre usuários;
- gerar ranking de usuários com base no progresso do álbum e nas trocas aceitas.

Novos componentes sugeridos:
- TradeSuggestionService
  - responsável por analisar coleções e sugerir trocas possíveis.

- RankingService
  - responsável por calcular o ranking dos usuários.

- TradeSuggestion
  - representa uma sugestão de troca.
  - campos sugeridos:
    - userAId
    - userBId
    - stickerOfferedByUserA
    - stickerOfferedByUserB
    - reason

- UserRankingPosition
  - representa a posição de um usuário no ranking.
  - campos sugeridos:
    - userId
    - userName
    - uniqueStickersCount
    - albumCompletionPercentage
    - acceptedTradesCount
    - position

Regras técnicas para sugestão automática:
- Obter a coleção de todos os usuários.
- Identificar figurinhas repetidas de cada usuário.
- Identificar figurinhas ausentes de cada usuário.
- Comparar usuários em pares.
- Sugerir troca quando:
  - usuário A possui repetida que usuário B não possui;
  - usuário B possui repetida que usuário A não possui;
  - a troca é de uma figurinha por uma figurinha.
- Não alterar coleções.
- Não criar propostas automaticamente.
- Retornar apenas sugestões.

Regras técnicas para ranking:
- Calcular quantidade de figurinhas únicas por usuário.
- Calcular percentual de conclusão do álbum.
- Calcular quantidade de trocas aceitas por usuário.
- Ordenar usuários por percentual de conclusão.
- Usar quantidade de trocas aceitas como critério secundário, se necessário.
- Retornar lista ordenada com posições.

Informações necessárias:
- Definir total de figurinhas do álbum.
- O total pode ser configurável ou definido como constante para fins didáticos.
- O cálculo de conclusão deve usar:
  - figurinhas únicas do usuário / total de figurinhas do álbum * 100.

Serviços impactados:
- CollectionService
  - expor consulta de figurinhas únicas.
  - expor consulta de figurinhas ausentes, se necessário.
  - expor consulta de repetidas.

- TradeService
  - expor consulta de trocas aceitas por usuário, se necessário.

Novos endpoints ou comandos:
- Consultar sugestões automáticas de troca.
- Consultar ranking de usuários.

Testes esperados:
- Deve sugerir troca quando dois usuários possuem repetidas úteis um para o outro.
- Não deve sugerir troca quando apenas um usuário se beneficia.
- Não deve sugerir troca com figurinha única, caso a regra esteja ativa.
- Não deve alterar coleções ao gerar sugestões.
- Deve calcular percentual de conclusão do álbum.
- Deve ordenar ranking por progresso no álbum.
- Deve usar trocas aceitas como critério secundário quando aplicável.

Gere um plano técnico incremental, simples e adequado para treinamento.
```

---

## 12. Prompt — Tasks da Evolução 2

```text
/speckit.tasks

Com base na especificação e no plano técnico da Evolução 2, gere as tarefas necessárias para implementar sugestão automática de trocas e ranking de usuários.

Organize as tarefas em passos pequenos, claros e executáveis.

Escopo da Evolução 2:
- sugestão automática de trocas;
- ranking de usuários.

Tarefas esperadas:

1. Preparar modelo para sugestões
   - Criar estrutura TradeSuggestion.
   - Definir campos da sugestão:
     - usuário A;
     - usuário B;
     - figurinha oferecida pelo usuário A;
     - figurinha oferecida pelo usuário B;
     - motivo da sugestão.

2. Criar TradeSuggestionService
   - Criar serviço responsável por gerar sugestões.
   - Obter coleções dos usuários.
   - Identificar repetidas de cada usuário.
   - Identificar figurinhas ausentes de cada usuário.
   - Comparar usuários em pares.
   - Gerar sugestão quando ambos os usuários se beneficiam.
   - Garantir que a sugestão não altera coleções.
   - Garantir que a sugestão não cria proposta automaticamente.

3. Aplicar regras de sugestão
   - Sugerir apenas troca de uma figurinha por uma figurinha.
   - Sugerir apenas quando cada usuário oferece figurinha repetida.
   - Respeitar regra de não trocar figurinha única, caso esteja ativa.
   - Evitar sugestões duplicadas.

4. Criar endpoint ou comando de sugestões
   - Criar endpoint para listar sugestões automáticas.
   - Permitir consultar sugestões gerais.
   - Opcionalmente permitir consultar sugestões para um usuário específico.

5. Preparar modelo para ranking
   - Criar estrutura UserRankingPosition.
   - Definir campos:
     - posição;
     - usuário;
     - quantidade de figurinhas únicas;
     - percentual de conclusão;
     - quantidade de trocas aceitas.

6. Criar RankingService
   - Calcular quantidade de figurinhas únicas por usuário.
   - Calcular percentual de conclusão do álbum.
   - Consultar quantidade de trocas aceitas por usuário.
   - Ordenar usuários por percentual de conclusão.
   - Aplicar critério secundário por quantidade de trocas aceitas.
   - Gerar posições do ranking.

7. Criar endpoint ou comando de ranking
   - Criar endpoint para consultar ranking geral.
   - Retornar lista ordenada dos usuários.
   - Exibir dados principais do progresso no álbum.

8. Testes de sugestão automática
   - Testar geração de sugestão válida entre dois usuários.
   - Testar ausência de sugestão quando não há benefício mútuo.
   - Testar ausência de sugestão quando não existem repetidas.
   - Testar que sugestões não alteram coleções.
   - Testar que sugestões não criam propostas automaticamente.
   - Testar remoção de sugestões duplicadas.

9. Testes de ranking
   - Testar cálculo de figurinhas únicas.
   - Testar cálculo de percentual de conclusão.
   - Testar ordenação por progresso no álbum.
   - Testar critério secundário por trocas aceitas.
   - Testar retorno das posições corretamente.

10. Revisão final
   - Garantir compatibilidade com MVP e Evolução 1.
   - Garantir que sugestões são apenas consultivas.
   - Garantir que ranking não altera dados do sistema.
   - Manter código simples e adequado ao treinamento.

Gere as tarefas em formato numerado, objetivo e pronto para execução.
```

---

## 13. Prompt — Implement da Evolução 2

```text
/speckit.implement

Implemente a Evolução 2 do sistema de troca de figurinhas da Copa com base nas tarefas geradas.

Contexto:
O sistema já possui:
- cadastro de usuários;
- coleção de figurinhas;
- controle de repetidas;
- proposta de troca;
- aceite e recusa de troca;
- validações de negócio;
- atualização de coleções após troca aceita.

Objetivo da implementação:
Adicionar sugestão automática de trocas e ranking de usuários.

Funcionalidades a implementar:

1. Sugestão automática de trocas
   - Criar estrutura TradeSuggestion.
   - Criar TradeSuggestionService.
   - Analisar coleções dos usuários.
   - Identificar figurinhas repetidas.
   - Identificar figurinhas ausentes.
   - Gerar sugestões quando dois usuários puderem se beneficiar.
   - Garantir que a sugestão não altere coleções.
   - Garantir que a sugestão não crie proposta automaticamente.
   - Evitar sugestões duplicadas.

2. Ranking de usuários
   - Criar estrutura UserRankingPosition.
   - Criar RankingService.
   - Calcular quantidade de figurinhas únicas por usuário.
   - Calcular percentual de conclusão do álbum.
   - Calcular quantidade de trocas aceitas por usuário.
   - Ordenar ranking por percentual de conclusão.
   - Usar quantidade de trocas aceitas como critério secundário.
   - Retornar lista ordenada com posições.

Regras obrigatórias:
- Uma sugestão só deve existir quando ambos os usuários se beneficiam.
- Cada usuário deve oferecer uma figurinha repetida.
- A sugestão deve ser apenas consultiva.
- O ranking deve ser apenas consultivo.
- Nenhuma dessas funcionalidades deve alterar coleções diretamente.
- O cálculo de percentual de conclusão deve considerar figurinhas únicas.
- O total de figurinhas do álbum deve estar configurado de forma simples e clara.

Restrições:
- Não implementar machine learning.
- Não implementar notificações.
- Não implementar chat.
- Não implementar interface gráfica avançada.
- Não alterar comportamentos já existentes do MVP e da Evolução 1.

Critérios de qualidade:
- Código simples e didático.
- Serviços bem separados.
- Regras de negócio centralizadas.
- Testes cobrindo sugestões e ranking.
- Implementação adequada para demonstrar evolução incremental no treinamento.

Implemente as tarefas na ordem definida e garanta que todos os testes existentes continuem passando.
```

---

# Sugestão de uso no treinamento

Você pode conduzir o treinamento como uma jornada incremental:

```text
MVP:
Da ideia ao primeiro sistema funcional.

Evolução 1:
Adição de regras reais de negócio e controle de estado.

Evolução 2:
Evolução para funcionalidades inteligentes e engajamento.
```

Mensagem principal:

> O Spec Kit permite transformar uma ideia simples em um sistema evolutivo, onde cada fase adiciona contexto, regras e implementação de forma controlada.

---

# Estrutura sugerida do repositório

```text
.
├── README.md
├── prompts/
│   ├── 01-mvp-constitution.md
│   ├── 02-mvp-specify.md
│   ├── 03-mvp-plan.md
│   ├── 04-mvp-tasks.md
│   ├── 05-mvp-implement.md
│   ├── 06-evolucao-1-specify.md
│   ├── 07-evolucao-1-plan.md
│   ├── 08-evolucao-1-tasks.md
│   ├── 09-evolucao-1-implement.md
│   ├── 10-evolucao-2-specify.md
│   ├── 11-evolucao-2-plan.md
│   ├── 12-evolucao-2-tasks.md
│   └── 13-evolucao-2-implement.md
└── docs/
    └── roteiro-treinamento.md
```

---

# Observações finais

Este material foi pensado para facilitar o uso dos prompts durante um treinamento prático.

A proposta é que cada fase seja executada de forma incremental, permitindo demonstrar como o Spec Kit ajuda a transformar requisitos em especificações, planos, tarefas e código de forma estruturada.
