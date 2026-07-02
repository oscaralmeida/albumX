# Quickstart: MVP de Troca de Figurinhas da Copa

**Feature**: `001-sticker-trade-mvp`  
**Branch de implementação**: `feature/mvp-base`  
**Plano**: [plan.md](./plan.md) | **Contrato**: [contracts/openapi.yaml](./contracts/openapi.yaml)

## Pré-requisitos

**Opção A — Docker (recomendado para treinamento)**

- Docker Desktop 4.x+ com Docker Compose

**Opção B — Desenvolvimento local**

- JDK 21+
- Maven 3.9+
- Node.js 20+ e npm

## Execução com Docker (fluxo principal)

```bash
# Na raiz do repositório, branch feature/mvp-base
docker-compose up --build
```

| Serviço | URL |
|---------|-----|
| **Frontend (interface web)** | http://localhost:3000 |
| **API REST** | http://localhost:8080 |
| **Swagger UI** | http://localhost:8080/swagger-ui.html |

Aguarde os healthchecks ou mensagens de "Started AlbumXApplication" antes de acessar.

## Execução local (sem Docker)

### Backend

```bash
mvn clean package
mvn spring-boot:run
```

API em `http://localhost:8080`. Swagger em `http://localhost:8080/swagger-ui.html`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Interface em `http://localhost:5173` (Vite dev server). Configure `VITE_API_URL=http://localhost:8080` se necessário.

## Executar testes do backend

```bash
mvn test
```

Esperado: todos os testes de domínio e integração passando.

## Cenário de validação via browser (fluxo completo)

Objetivo: demonstrar SC-005 e SC-006 em menos de 5 minutos, **sem Postman nem curl**.

### 1. Cadastrar colecionadores

1. Acesse http://localhost:3000
2. Na seção **Usuários**, cadastre "Alice" e depois "Bob"
3. Confirme que cada cadastro exibe o ID gerado

### 2. Registrar coleção

1. Selecione **Alice** como usuário ativo
2. Adicione figurinha **10** (duas vezes) e figurinha **25** (uma vez)
3. Visualize a coleção: figurinha 10 com quantidade 2, figurinha 25 com quantidade 1

### 3. Consultar repetidas

1. Acesse a seção **Repetidas** para Alice
2. Confirme que apenas a figurinha **10** aparece (quantidade 2)

### 4. Criar proposta de troca válida

1. Na seção **Trocas**, crie proposta:
   - Solicitante: Alice
   - Destinatário: Bob
   - Oferece: 10
   - Deseja: 50
2. Confirme mensagem de sucesso e status **PROPOSED**
3. Verifique que a coleção de Alice permanece inalterada

### 5. Listar propostas

1. Na lista de propostas, confirme a proposta criada acima

### 6. Cenário de erro — figurinha não possuída

1. Tente criar proposta com Alice oferecendo figurinha **99** (não possui)
2. Confirme mensagem de erro compreensível na interface (sem quebrar a página)

## Validação via Swagger

1. Acesse http://localhost:8080/swagger-ui.html
2. Execute cada endpoint do MVP conforme [contracts/openapi.yaml](./contracts/openapi.yaml)
3. Confirme respostas coerentes com os cenários acima (SC-008)

## Validação via API (opcional — instrutores)

Substitua `{USER_A}`, `{USER_B}` pelos UUIDs retornados.

```bash
curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" -d '{"name":"Alice"}'

curl -s -X POST http://localhost:8080/api/users/{USER_A}/collection/stickers \
  -H "Content-Type: application/json" -d '{"stickerNumber":10}'
```

Detalhes completos dos cenários curl permanecem disponíveis para depuração; não são necessários para o treinamento.

## Checklist de aceite do MVP

- [ ] `docker-compose up` sobe frontend e backend
- [ ] Cadastro de usuário com ID único via browser
- [ ] Adição de figurinha incrementa quantidade via browser
- [ ] Repetidas retornam apenas quantidade > 1 via browser
- [ ] Proposta válida criada com status PROPOSED via browser
- [ ] Proposta inválida (sem posse) rejeitada com mensagem clara
- [ ] Swagger acessível e endpoints invocáveis
- [ ] `mvn test` verde

## Referências

- Regras e entidades: [data-model.md](./data-model.md)
- Decisões técnicas: [research.md](./research.md)
- Especificação funcional: [spec.md](./spec.md)
