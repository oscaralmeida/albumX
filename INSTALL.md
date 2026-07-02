# Spec Kit — Referência e Instalação

Este repositório utiliza o **[Spec Kit](https://github.com/github/spec-kit)**, toolkit open-source do GitHub para **Spec-Driven Development (SDD)** — desenvolvimento orientado por especificação.

Com o Spec Kit, o fluxo do projeto segue etapas como constituição, especificação, planejamento, tarefas e implementação (comandos `/speckit.*` no agente de IA). Os artefatos ficam versionados em `.specify/` e em `specs/`.

**Repositório oficial:** [github.com/github/spec-kit](https://github.com/github/spec-kit)

---

## Pré-requisitos

Antes de instalar, confirme que o ambiente possui:

| Requisito | Observação |
|-----------|------------|
| **Python 3.11+** | Necessário para o CLI `specify` |
| **Git** | Clonagem de repositórios e integração com o Spec Kit |
| **Agente de IA** | Ex.: Claude, Cursor Agent — para executar os comandos `/speckit.*` |
| **`uv`** | Gerenciador de pacotes Python usado pelo Spec Kit |

**Instalação do `uv`:** [documentação oficial](https://github.com/github/spec-kit/blob/main/docs/install/uv.md)

---

## Instalação global (recomendada)

Instala o comando `specify` de forma persistente no sistema:

```bash
uv tool install specify-cli --from git+https://github.com/github/spec-kit.git
```

Verifique a instalação:

```bash
specify --version
```

### Problemas no Windows

Se a instalação global falhar (permissões ou caminho de ferramentas), defina um diretório alternativo antes de repetir o comando:

```powershell
$env:UV_TOOL_DIR = "C:\uv-tools"
uv tool install specify-cli --from git+https://github.com/github/spec-kit.git
```

Para tornar `UV_TOOL_DIR` permanente na sessão do usuário, adicione a variável nas configurações de ambiente do Windows ou ao seu perfil do PowerShell.

---

## Instalação de teste (sem instalar globalmente)

Use `uvx` para executar o CLI uma vez, útil para validar o ambiente ou experimentar em outro diretório:

```bash
uvx --from git+https://github.com/github/spec-kit.git specify init --force life-os
```

> **Nota:** `life-os` é apenas um nome de exemplo de projeto. Para este repositório (AlbumX), o Spec Kit já está configurado em `.specify/` — não é necessário rodar `specify init` novamente, a menos que esteja recriando a estrutura do zero.

---

## Uso neste projeto (AlbumX)

Após o ambiente configurado:

1. Abra o repositório no seu editor com agente de IA (ex.: Cursor).
2. Use os comandos Spec Kit conforme a fase do treinamento:
   - `/speckit.constitution` — princípios do projeto
   - `/speckit.specify` — especificação funcional
   - `/speckit.plan` — planejamento técnico
   - `/speckit.tasks` — lista de tarefas
   - `/speckit.implement` — implementação
3. Consulte `README.md` para o roteiro completo do treinamento e os prompts por fase (MVP, Evolução 1, Evolução 2).

---

## Referências

- [Spec Kit — repositório](https://github.com/github/spec-kit)
- [Instalação do `uv`](https://github.com/github/spec-kit/blob/main/docs/install/uv.md)
- [README deste projeto](./README.md) — prompts e fluxo didático do AlbumX
