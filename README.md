# 📅 API de Gerenciamento de Eventos

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-27-orange.svg)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![H2](https://img.shields.io/badge/Database-H2-blue.svg)](https://www.h2database.com/)
[![OpenAPI](https://img.shields.io/badge/Swagger-OpenAPI%203.1-85EA2D.svg)](https://springdoc.org/)

API RESTful desenvolvida em **Spring Boot 4** para gerenciamento completo de **eventos**, **locais**, **palestrantes**, **participantes** e **inscrições**.

---

## 📋 Sumário

- [Visão Geral](#-visão-geral)
- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Modelo de Dados](#-modelo-de-dados)
- [Endpoints](#-endpoints)
- [Como Executar](#-como-executar)
- [Como Testar](#-como-testar)
- [Documentação Swagger](#-documentação-swagger)
- [Collection Postman](#-collection-postman)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Requisitos Atendidos](#-requisitos-atendidos)
- [Autor](#-autor)

---

## 🎯 Visão Geral

Este projeto implementa uma **API RESTful completa** para o domínio de **Gerenciamento de Eventos**, demonstrando na prática:

- ✅ **Arquitetura em camadas** (Controller → Service → Repository)
- ✅ **5 entidades JPA** com relacionamentos `@OneToOne`, `@OneToMany` e `@ManyToMany`
- ✅ **CRUD completo** com códigos HTTP apropriados
- ✅ **Paginação** em todas as listagens (`Pageable` + `PagedModel`)
- ✅ **HATEOAS** com links de navegação entre recursos
- ✅ **Bean Validation** em todas as entidades
- ✅ **Documentação OpenAPI/Swagger** interativa
- ✅ **Consultas personalizadas** em cada entidade
- ✅ **Tratamento global de erros** com `@RestControllerAdvice`

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Descrição |
|------------|:------:|-----------|
| **Java** | 27 | Linguagem de programação |
| **Spring Boot** | 4.1.1 | Framework principal |
| **Spring Data JPA** | 4.1.1 | ORM e persistência |
| **Spring HATEOAS** | 3.1.2 | Navegabilidade entre recursos |
| **Spring Validation** | 4.1.1 | Bean Validation (JSR-380) |
| **Springdoc OpenAPI** | 3.1.1 | Documentação Swagger UI |
| **H2 Database** | 2.4.240 | Banco de dados em memória |
| **Hibernate** | 7.4.5 | Provider JPA |
| **Maven** | Wrapper | Gerenciador de dependências |

---

## 🏗️ Arquitetura

O projeto segue o padrão de **arquitetura em camadas**:

```
┌──────────────────────────────────────────────────────────┐
│                       Cliente                             │
│                (Postman, Navegador, cURL)                 │
└────────────────────────┬─────────────────────────────────┘
                         │ HTTP
                         ▼
┌──────────────────────────────────────────────────────────┐
│  Controller (@RestController)                             │
│  • Recebe requisições HTTP                                │
│  • Valida entrada (@Valid)                                │
│  • Retorna ResponseEntity com HATEOAS                     │
└────────────────────────┬─────────────────────────────────┘
                         ▼
┌──────────────────────────────────────────────────────────┐
│  Service (@Service)                                       │
│  • Regras de negócio                                      │
│  • Transações (@Transactional)                            │
│  • Validações de duplicidade                              │
└────────────────────────┬─────────────────────────────────┘
                         ▼
┌──────────────────────────────────────────────────────────┐
│  Repository (JpaRepository)                               │
│  • CRUD automático                                        │
│  • Consultas derivadas (findBy...)                        │
└────────────────────────┬─────────────────────────────────┘
                         ▼
┌──────────────────────────────────────────────────────────┐
│              Banco de Dados H2 (memória)                  │
└──────────────────────────────────────────────────────────┘
```

---

## 🗂️ Modelo de Dados

### Diagrama de Entidades

```
   ┌────────────┐  1:1   ┌─────────────────┐  N:M   ┌────────────────┐
   │   Local    │◄──────►│     Evento      │◄──────►│  Palestrante   │
   └────────────┘        └────────┬────────┘        └────────────────┘
                                  │ 1
                                  │
                                  │ N
                          ┌───────▼─────────┐
                          │    Inscricao    │
                          └───────┬─────────┘
                                  │ N
                                  │
                                  │ 1
                          ┌───────▼─────────┐
                          │  Participante   │
                          └─────────────────┘
```

### Entidades

| Entidade | Campos Principais | Relacionamentos |
|----------|-------------------|-----------------|
| **Evento** | `id`, `titulo`, `data`, `status` | `@OneToOne` (Local), `@OneToMany` (Inscricao), `@ManyToMany` (Palestrante) |
| **Local** | `id`, `nome`, `endereco`, `cidade` | `@OneToOne(mappedBy)` (Evento) |
| **Palestrante** | `id`, `nome`, `email`, `especialidade` | `@ManyToMany(mappedBy)` (Evento) |
| **Participante** | `id`, `nome`, `email`, `telefone` | `@OneToMany` (Inscricao) |
| **Inscricao** | `id`, `dataInscricao`, `status` | `@ManyToOne` (Evento, Participante) |

### Enum

```java
public enum StatusEvento {
    PLANEJADO, CONFIRMADO, EM_ANDAMENTO, CONCLUIDO, CANCELADO
}
```

---

## 🌐 Endpoints

A API expõe **39 endpoints** distribuídos em **5 recursos**:

### 📅 Eventos — `/eventos`

| Método | Endpoint | Descrição |
|:------:|----------|-----------|
| `GET` | `/eventos?page=0&size=10` | Lista paginada |
| `GET` | `/eventos/{id}` | Busca por ID |
| `POST` | `/eventos` | Cria novo evento |
| `PUT` | `/eventos/{id}` | Atualiza evento |
| `DELETE` | `/eventos/{id}` | Deleta evento |
| `GET` | `/eventos/buscar?titulo=X` | Busca por título |
| `GET` | `/eventos/status/{status}` | Filtra por status |
| `GET` | `/eventos/cidade/{cidade}` | Filtra por cidade do local |
| `GET` | `/eventos/periodo?inicio=X&fim=Y` | Filtra por intervalo de datas |

### 🏢 Locais — `/locais`

| Método | Endpoint | Descrição |
|:------:|----------|-----------|
| `GET` | `/locais` | Lista paginada |
| `GET` | `/locais/{id}` | Busca por ID |
| `POST` | `/locais` | Cria novo local |
| `PUT` | `/locais/{id}` | Atualiza local |
| `DELETE` | `/locais/{id}` | Deleta local |
| `GET` | `/locais/buscar?cidade=X` | Busca por cidade |
| `GET` | `/locais/nome?nome=X` | Busca por nome |
| `GET` | `/locais/endereco?endereco=X` | Busca por endereço |

### 🎤 Palestrantes — `/palestrantes`

| Método | Endpoint | Descrição |
|:------:|----------|-----------|
| `GET` | `/palestrantes` | Lista paginada |
| `GET` | `/palestrantes/{id}` | Busca por ID |
| `POST` | `/palestrantes` | Cria novo palestrante |
| `PUT` | `/palestrantes/{id}` | Atualiza palestrante |
| `DELETE` | `/palestrantes/{id}` | Deleta palestrante |
| `GET` | `/palestrantes/nome?nome=X` | Busca por nome |
| `GET` | `/palestrantes/especialidade?especialidade=X` | Busca por especialidade |

### 👥 Participantes — `/participantes`

| Método | Endpoint | Descrição |
|:------:|----------|-----------|
| `GET` | `/participantes` | Lista paginada |
| `GET` | `/participantes/{id}` | Busca por ID |
| `POST` | `/participantes` | Cria novo participante |
| `PUT` | `/participantes/{id}` | Atualiza participante |
| `DELETE` | `/participantes/{id}` | Deleta participante |
| `GET` | `/participantes/buscar?nome=X` | Busca por nome |

### 📝 Inscrições — `/inscricoes`

| Método | Endpoint | Descrição |
|:------:|----------|-----------|
| `GET` | `/inscricoes` | Lista paginada |
| `GET` | `/inscricoes/{id}` | Busca por ID |
| `POST` | `/inscricoes` | Cria nova inscrição |
| `PUT` | `/inscricoes/{id}` | Atualiza inscrição |
| `DELETE` | `/inscricoes/{id}` | Deleta inscrição |
| `GET` | `/inscricoes/status?status=X` | Filtra por status |
| `GET` | `/inscricoes/evento/{eventoId}` | Filtra por evento |
| `GET` | `/inscricoes/participante/{participanteId}` | Filtra por participante |
| `GET` | `/inscricoes/periodo?inicio=X&fim=Y` | Filtra por intervalo |

---

## 🚀 Como Executar

### Pré-requisitos

- **Java 17+** instalado (recomendado: Java 21+)
- **Maven** (ou usar o wrapper incluído)
- **Git** (opcional)

### Passo a passo

**1. Clone o repositório**

```bash
git clone https://github.com/danielfontz/gerenciamento-eventos.git
cd gerenciamento-eventos
```

**2. Execute a aplicação**

Via Maven Wrapper:

```bash
# Linux/macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

Ou rode direto pela IDE (IntelliJ):
- Abra o projeto
- Execute a classe `GerenciamentoEventosApplication`

**3. Acesse os recursos disponíveis**

| Recurso | URL |
|---------|-----|
| API REST | http://localhost:8080 |
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| H2 Console | http://localhost:8080/h2-console |

### Acesso ao H2 Console

| Campo | Valor |
|-------|-------|
| JDBC URL | `jdbc:h2:mem:eventosdb` |
| User Name | `sa` |
| Password | *(vazio)* |

---

## 🧪 Como Testar

### Via Swagger UI (recomendado)

1. Acesse http://localhost:8080/swagger-ui.html
2. Escolha um endpoint (ex: `GET /eventos`)
3. Clique em **Try it out** → **Execute**
4. Veja a resposta com links HATEOAS

### Via Postman

Importe a Collection disponível em `docs/postman/` (veja a seção [Collection Postman](#-collection-postman)).

### Exemplos com cURL

#### Criar um Local

```bash
curl -X POST http://localhost:8080/locais \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Centro de Convenções",
    "endereco": "Av. Paulista, 1000",
    "cidade": "São Paulo"
  }'
```

**Resposta (201 Created):**

```json
{
  "id": 1,
  "nome": "Centro de Convenções",
  "endereco": "Av. Paulista, 1000",
  "cidade": "São Paulo",
  "_links": {
    "self": { "href": "http://localhost:8080/locais/1" },
    "locais": { "href": "http://localhost:8080/locais" },
    "update": { "href": "http://localhost:8080/locais/1" },
    "delete": { "href": "http://localhost:8080/locais/1" }
  }
}
```

#### Criar um Evento

```bash
curl -X POST http://localhost:8080/eventos \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Workshop de Spring Boot",
    "data": "2026-12-15",
    "status": "PLANEJADO"
  }'
```

#### Criar uma Inscrição (com HATEOAS navegável)

```bash
curl -X POST http://localhost:8080/inscricoes \
  -H "Content-Type: application/json" \
  -d '{
    "dataInscricao": "2026-11-01T10:00:00",
    "status": "ATIVA",
    "evento": { "id": 1 },
    "participante": { "id": 1 }
  }'
```

**Resposta (201 Created):**

```json
{
  "id": 1,
  "dataInscricao": "2026-11-01T10:00:00",
  "status": "ATIVA",
  "evento": { "id": 1, "titulo": "Workshop de Spring Boot" },
  "participante": { "id": 1, "nome": "João Souza" },
  "_links": {
    "self":         { "href": "http://localhost:8080/inscricoes/1" },
    "inscricoes":   { "href": "http://localhost:8080/inscricoes" },
    "update":       { "href": "http://localhost:8080/inscricoes/1" },
    "delete":       { "href": "http://localhost:8080/inscricoes/1" },
    "evento":       { "href": "http://localhost:8080/eventos/1" },
    "participante": { "href": "http://localhost:8080/participantes/1" }
  }
}
```

---

## 📚 Documentação Swagger

A documentação interativa é gerada automaticamente com **Springdoc OpenAPI 3.1.1** e está disponível em:

🔗 **http://localhost:8080/swagger-ui.html**

### Recursos da documentação

- ✅ Descrição detalhada de cada endpoint
- ✅ Exemplos de requisição e resposta
- ✅ Todos os códigos HTTP possíveis (200, 201, 204, 400, 404)
- ✅ Regras de negócio documentadas
- ✅ Botão **Try it out** para testar direto na UI
- ✅ Barra de busca para filtrar endpoints
- ✅ Tema customizado (CSS próprio)

---

## 📮 Collection Postman

A collection completa com os 39 endpoints está disponível em:

- `docs/postman/Gerenciamento-Eventos.postman_collection.json`
- `docs/postman/Local.postman_environment.json`

### Como importar

1. Abra o Postman
2. Clique em **Import** → **File** → selecione os 2 arquivos JSON
3. Selecione o ambiente **Local** no dropdown superior direito
4. Execute os requests individualmente ou a collection inteira

### Como executar a collection inteira

1. Botão direito na collection → **Run collection**
2. Clique em **Run 🎯 Gerenciamento de Eventos — API**
3. Veja os 39 requests rodando e um relatório no final

---

## 📁 Estrutura do Projeto

```
gerenciamento-eventos/
├── docs/
│   └── postman/
│       ├── Gerenciamento-Eventos.postman_collection.json
│       └── Local.postman_environment.json
│
├── src/
│   ├── main/
│   │   ├── java/com/danielfontz/gerenciamento_eventos/
│   │   │   ├── GerenciamentoEventosApplication.java   ← Classe principal
│   │   │   ├── LoadDatabase.java                      ← Seed de dados
│   │   │   │
│   │   │   ├── config/                                ← Configurações
│   │   │   │   └── OpenApiConfig.java                 ← Metadados Swagger
│   │   │   │
│   │   │   ├── model/                                 ← Entidades JPA
│   │   │   │   ├── Evento.java
│   │   │   │   ├── Local.java
│   │   │   │   ├── Palestrante.java
│   │   │   │   ├── Participante.java
│   │   │   │   ├── Inscricao.java
│   │   │   │   └── StatusEvento.java                  ← Enum
│   │   │   │
│   │   │   ├── repository/                            ← Repositórios Spring Data
│   │   │   │   ├── EventoRepository.java
│   │   │   │   ├── LocalRepository.java
│   │   │   │   ├── PalestranteRepository.java
│   │   │   │   ├── ParticipanteRepository.java
│   │   │   │   └── InscricaoRepository.java
│   │   │   │
│   │   │   ├── service/                               ← Regras de negócio
│   │   │   │   ├── EventoService.java
│   │   │   │   ├── LocalService.java
│   │   │   │   ├── PalestranteService.java
│   │   │   │   ├── ParticipanteService.java
│   │   │   │   └── InscricaoService.java
│   │   │   │
│   │   │   ├── assembler/                             ← HATEOAS
│   │   │   │   ├── EventoModelAssembler.java
│   │   │   │   ├── LocalModelAssembler.java
│   │   │   │   ├── PalestranteModelAssembler.java
│   │   │   │   ├── ParticipanteModelAssembler.java
│   │   │   │   └── InscricaoModelAssembler.java
│   │   │   │
│   │   │   ├── controller/                            ← Endpoints REST
│   │   │   │   ├── EventoController.java
│   │   │   │   ├── LocalController.java
│   │   │   │   ├── PalestranteController.java
│   │   │   │   ├── ParticipanteController.java
│   │   │   │   └── InscricaoController.java
│   │   │   │
│   │   │   └── exception/                             ← Tratamento de erros
│   │   │       ├── EventoNotFoundException.java
│   │   │       ├── LocalNotFoundException.java
│   │   │       ├── PalestranteNotFoundException.java
│   │   │       ├── ParticipanteNotFoundException.java
│   │   │       ├── InscricaoNotFoundException.java
│   │   │       └── *Advice.java                       ← Handlers globais
│   │   │
│   │   └── resources/
│   │       ├── application.properties                 ← Configurações
│   │       └── static/
│   │           └── swagger-custom.css                 ← Tema Swagger
│   │
│   └── test/
│       └── java/...                                    ← Testes
│
├── .gitignore
├── pom.xml
└── README.md
```

---

## ✅ Requisitos Atendidos

| Requisito | Implementação |
|-----------|---------------|
| Spring Boot versão mais recente | ✅ 4.1.1 |
| Java 17+ | ✅ Java 27 |
| Maven como gerenciador | ✅ Maven Wrapper |
| H2 para persistência | ✅ Configurado |
| Spring Data JPA | ✅ 5 repositórios |
| Mínimo 5 entidades | ✅ 5 entidades |
| `@OneToOne` | ✅ Evento ↔ Local |
| `@OneToMany` | ✅ Evento → Inscricao, Participante → Inscricao |
| `@ManyToMany` | ✅ Evento ↔ Palestrante |
| Bean Validation | ✅ Em todas as entidades |
| Enum | ✅ `StatusEvento` |
| 5+ endpoints por entidade | ✅ 39 endpoints no total |
| CRUD completo | ✅ Todas as entidades |
| Paginação | ✅ Em todas as listagens |
| Consultas personalizadas | ✅ Múltiplas por entidade |
| Códigos HTTP corretos | ✅ 200, 201, 204, 400, 404 |
| Swagger/OpenAPI | ✅ Springdoc 3.1.1 |
| HATEOAS | ✅ `EntityModel`, `PagedModel` |

---

## 🎨 Recursos Extras

- 🎨 **Swagger UI customizado** com CSS próprio
- 📦 **Dados de exemplo** carregados automaticamente (`LoadDatabase`)
- 🔍 **Logs SQL** formatados no console (útil para debug)
- 📝 **Tratamento global de exceções** com mensagens amigáveis
- 📮 **Collection Postman** pronta para importar

---

## 📄 Licença

Este projeto está licenciado sob a **MIT License**.

---

## 👨‍💻 Autor

**Daniel Fontz**

- GitHub: [@danielfontz](https://github.com/danielfontz)
- E-mail: daniel@exemplo.com

---

<p align="center">
  Desenvolvido com ☕ e Spring Boot 🍃
</p>
