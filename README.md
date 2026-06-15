# MAP – Meu Avatar Preventivo

Care Plus Challenge – SOA e WebServices Sprint 4

---

## Integrantes

* **Gilson Dias Ramos Junior - RM552345**
* **Jeferson Gabriel de Mendonça - RM553149**
* **Larissa Estella Gonçalves dos Santos - RM552695**

---

## Sumário

1. [Descrição do Projeto](#descrição-do-projeto)
2. [Tecnologias](#tecnologias)
3. [Como Executar](#como-executar)
4. [Como Rodar os Testes](#testes)
5. [Princípios SOLID Aplicados](#princípios-solid-aplicados)
6. [Segurança](#segurança)
7. [Credenciais Padrão](#credenciais-padrão)
8. [Autenticação](#autenticação)
9. [Endpoints e cURLs](#endpoints-e-curls)
10. [Estrutura do Projeto](#estrutura-do-projeto)

---

## Descrição do Projeto

API REST para o módulo de gamificação da plataforma **Care Plus**. Os usuários criam avatares digitais e os
evoluem completando missões de saúde preventiva (hidratação, sono, exercício, bem-estar e saúde).
O projeto implementa autenticação segura com JWT, arquitetura em camadas com princípios SOLID, documentação
automática via Swagger e cobertura de testes unitários e de integração.

---

## Tecnologias

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.2.5 | Framework base |
| Spring Security | 6.2.x | Autenticação e autorização |
| JWT (jjwt) | 0.11.5 | Geração e validação de tokens |
| Spring Data JPA | 3.2.x | Persistência de dados |
| Hibernate | 6.4.x | ORM |
| Flyway | 9.x | Migrações de banco de dados |
| H2 | 2.2.x | Banco em memória (dev/test) |
| SpringDoc / Swagger UI | 2.5.0 | Documentação automática da API |
| Lombok | 1.18.x | Redução de boilerplate |
| JUnit 5 + Mockito | 5.x | Testes unitários e de integração |

---

## Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+

### 1. Clone o repositório

```bash
git clone https://github.com/larissaestella/SOA_SPRINT4.git
cd SOA_SPRINT4
```

### 2. Execute a aplicação

```bash
MapApplication.java
```

A aplicação sobe em `http://localhost:8080` com banco H2 em memória.  
As migrations do Flyway criam as tabelas e inserem os dados iniciais (missões e admin) automaticamente.

### 3. Acesse o Swagger UI

```
http://localhost:8080/swagger-ui.html
```

Para testar endpoints protegidos no Swagger:
1. Faça login em `POST /api/v1/auth/login`
2. Copie o token da resposta
3. Clique no botão **Authorize** no topo da página
4. Cole no formato: `Bearer <seu_token>`

### 4. Console H2 (banco de dados em memória)

```
URL:      http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:mapdb
Usuário:  sa
Senha:    (deixar vazio)
```

---

## Testes

```bash
# Todos os testes (unitários + integração)
mvn test

# Apenas testes unitários de service
mvn test -Dtest="*ServiceImplTest"

# Apenas testes de integração de controller
mvn test -Dtest="*IntegrationTest"
```

Os testes também podem ser executados diretamente pela IDE, 
selecione o teste que deseja executar, clique com o botão direito
e selecione Run.

Os resultados da execução serão exibidos na aba **Run** da IDE.


### Cobertura de testes

| Arquivo | Tipo | Cenários |
|---|---|---|
| `AuthServiceImplTest` | Unitário | Registrar, e-mail duplicado, autenticar com JWT |
| `AvatarServiceImplTest` | Unitário | Completar missão, bônus, level-up, missão inativa |
| `MissaoServiceImplTest` | Unitário | Criar, listar, filtrar por categoria, buscar, atualizar, desativar |
| `UsuarioServiceImplTest` | Unitário | Listar, buscar, atualizar, trocar e-mail, remover |
| `AuthControllerIntegrationTest` | Integração | Registro, login, e-mail duplicado, dados inválidos, senha errada |
| `AvatarControllerIntegrationTest` | Integração | Avatar, completar missão, estatísticas, ranking, 401 sem token |
| `MissaoControllerIntegrationTest` | Integração | CRUD, RBAC (ADMIN vs USUARIO), 401 sem token, filtro categoria |
| `UsuarioControllerIntegrationTest` | Integração | CRUD, RBAC, 401 sem token, 403 sem permissão, 404 não encontrado |

**Total:** 53 cenários de teste (4 arquivos unitários + 4 arquivos de integração)

---

## Princípios SOLID Aplicados

| Princípio | Como foi aplicado |
|---|---|
| **S** — Single Responsibility | Cada classe tem uma única responsabilidade. Controller recebe/responde; Service contém a lógica; Repository acessa o banco |
| **O** — Open/Closed | Novos serviços podem ser adicionados implementando as interfaces sem modificar os controllers |
| **L** — Liskov Substitution | `AuthServiceImpl`, `AvatarServiceImpl` etc. são completamente substituíveis pelas suas interfaces `IAuthService`, `IAvatarService` etc. |
| **I** — Interface Segregation | Interfaces separadas por domínio: `IAuthService`, `IUsuarioService`, `IMissaoService`, `IAvatarService` |
| **D** — Dependency Inversion | Controllers e services dependem de **interfaces**, não de implementações concretas. O Spring injeta a implementação via IoC |

---

## Segurança

| Mecanismo | Implementação                                                                         |
|---|---------------------------------------------------------------------------------------|
| **Stateless** | `SessionCreationPolicy.STATELESS`, sem sessão HTTP                                    |
| **JWT** | Tokens assinados com HS256, validade de 24h, via `JwtService`                         |
| **Filtro JWT** | `JwtAuthenticationFilter` intercepta todas as requisições antes dos controllers       |
| **BCrypt** | Todas as senhas criptografadas com `BCryptPasswordEncoder`                            |
| **RBAC** | Controle de acesso por perfil (`ADMIN` / `USUARIO`) via `hasRole()` e `@PreAuthorize` |
| **401 vs 403** | Anônimo (sem token) → 401; Autenticado sem permissão → 403                            |

---

## Credenciais Padrão

O Flyway insere automaticamente um usuário **ADMIN** ao iniciar:

| Campo    | Valor                      |
|----------|----------------------------|
| E-mail   | `admin@careplus.com.br`    |
| Senha    | `Admin@CareP1us`           |
| Perfil   | `ADMIN`                    |

Use essas credenciais para obter um token com permissões totais (criar/editar missões, listar/remover usuários).

---

## Autenticação

Todos os endpoints (exceto `/api/v1/auth/**`, Swagger e H2 console) exigem um token JWT no header:

```
Authorization: Bearer <token>
```

| Status | Situação |
|--------|----------|
| `401`  | Requisição sem token ou com token inválido/expirado |
| `403`  | Token válido, mas perfil sem permissão para o endpoint |

---

## Endpoints e cURLs

Substitua `<TOKEN>` pelo JWT obtido no login.

---

### Autenticação (público)

#### Registrar novo usuário
```bash
curl -X POST http://localhost:8080/api/v1/auth/registrar \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "senha": "Senha@123",
    "dataNascimento": "1995-06-15",
    "nomeAvatar": "JoaoAvatar"
  }'
```

#### Login (obter token JWT)
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com",
    "senha": "Senha@123"
  }'
```

#### Login como ADMIN
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@careplus.com.br",
    "senha": "Admin@CareP1us"
  }'
```

---

### Usuários (autenticado)

#### Buscar usuário por ID
```bash
curl -X GET http://localhost:8080/api/v1/usuarios/1 \
  -H "Authorization: Bearer <TOKEN>"
```

#### Atualizar dados do usuário
```bash
curl -X PUT http://localhost:8080/api/v1/usuarios/1 \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva Atualizado",
    "dataNascimento": "1995-06-15"
  }'
```

#### Listar todos os usuários (somente ADMIN)
```bash
curl -X GET http://localhost:8080/api/v1/usuarios \
  -H "Authorization: Bearer <TOKEN_ADMIN>"
```

#### Remover usuário (somente ADMIN)
```bash
curl -X DELETE http://localhost:8080/api/v1/usuarios/2 \
  -H "Authorization: Bearer <TOKEN_ADMIN>"
```

---

### Missões (autenticado; criar/editar/desativar somente ADMIN)

#### Listar missões ativas
```bash
curl -X GET http://localhost:8080/api/v1/missoes \
  -H "Authorization: Bearer <TOKEN>"
```

#### Listar missões filtradas por categoria
```bash
# Categorias: SAUDE, HIDRATACAO, SONO, EXERCICIO, BEM_ESTAR
curl -X GET "http://localhost:8080/api/v1/missoes?categoria=HIDRATACAO" \
  -H "Authorization: Bearer <TOKEN>"
```

#### Buscar missão por ID
```bash
curl -X GET http://localhost:8080/api/v1/missoes/1 \
  -H "Authorization: Bearer <TOKEN>"
```

#### Criar missão (somente ADMIN)
```bash
curl -X POST http://localhost:8080/api/v1/missoes \
  -H "Authorization: Bearer <TOKEN_ADMIN>" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Caminhada 30 minutos",
    "descricao": "Caminhe ao ar livre por pelo menos 30 minutos.",
    "categoria": "EXERCICIO",
    "pontosRecompensa": 20,
    "bonusExercicio": 25,
    "bonusSaude": 5
  }'
```

#### Atualizar missão (somente ADMIN)
```bash
curl -X PUT http://localhost:8080/api/v1/missoes/1 \
  -H "Authorization: Bearer <TOKEN_ADMIN>" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Caminhada 45 minutos",
    "descricao": "Caminhe ao ar livre por pelo menos 45 minutos.",
    "categoria": "EXERCICIO",
    "pontosRecompensa": 30,
    "bonusExercicio": 30,
    "bonusSaude": 10
  }'
```

#### Desativar missão — soft delete (somente ADMIN)
```bash
curl -X DELETE http://localhost:8080/api/v1/missoes/1 \
  -H "Authorization: Bearer <TOKEN_ADMIN>"
```

---

### Avatar & Gamificação (autenticado)

#### Consultar avatar do usuário
```bash
curl -X GET http://localhost:8080/api/v1/usuarios/1/avatar \
  -H "Authorization: Bearer <TOKEN>"
```

#### Completar uma missão
```bash
curl -X POST http://localhost:8080/api/v1/usuarios/1/missoes/completar \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "missaoId": 4,
    "observacao": "Bebi 2,5 litros hoje!"
  }'
```

#### Consultar estatísticas do usuário
```bash
curl -X GET http://localhost:8080/api/v1/usuarios/1/estatisticas \
  -H "Authorization: Bearer <TOKEN>"
```

#### Ranking global
```bash
curl -X GET http://localhost:8080/api/v1/ranking \
  -H "Authorization: Bearer <TOKEN>"
```

---

## Estrutura do Projeto

```
src/main/java/com/careplus/map/
├── config/
│   ├── CorsConfig.java              # Configuração de CORS
│   ├── OpenApiConfig.java           # Configuração Swagger/OpenAPI com JWT
│   └── SecurityConfig.java          # Segurança stateless, JWT, BCrypt, RBAC
│
├── controller/
│   ├── AuthController.java          # POST /auth/registrar, POST /auth/login
│   ├── AvatarController.java        # Avatar, missões, estatísticas, ranking
│   ├── MissaoController.java        # CRUD de missões
│   └── UsuarioController.java       # CRUD de usuários
│
├── exception/
│   ├── GlobalExceptionHandler.java  # Handler global: 400, 401, 403, 404, 422, 500
│   ├── RecursoNaoEncontradoException.java
│   └── RegraNegocioException.java
│
├── model/
│   ├── dto/                         # Data Transfer Objects (entrada e saída)
│   │   └── auth/                    # DTOs de autenticação
│   ├── entity/                      # Entidades JPA
│   ├── enums/                       # PerfilUsuario, CategoriaMissao
│   └── vo/                          # Value Objects (EstatisticasVO, RankingVO)
│
├── repository/                      # Interfaces JPA Repository
│
├── security/
│   ├── JwtAuthenticationFilter.java # Intercepta requisições e valida o token JWT
│   ├── JwtService.java              # Geração, validação e extração de claims JWT
│   └── UserDetailsServiceImpl.java  # Carrega usuário pelo e-mail para o Spring Security
│
└── service/
    ├── IAuthService.java            # Interface: registrar e autenticar
    ├── IAvatarService.java          # Interface: avatar, missão, estatísticas, ranking
    ├── IMissaoService.java          # Interface: CRUD de missões
    ├── IUsuarioService.java         # Interface: CRUD de usuários
    └── impl/
        ├── AuthServiceImpl.java
        ├── AvatarServiceImpl.java
        ├── MissaoServiceImpl.java
        └── UsuarioServiceImpl.java

src/main/resources/
├── application.properties
└── db/migration/
    ├── V1__create_tables.sql
    ├── V2__seed_missoes.sql
    ├── V3__add_auth_to_usuarios.sql
    └── V4__seed_admin.sql           

src/test/java/com/careplus/map/
├── controller/                      # Testes de integração (MockMvc)
│   ├── AuthControllerIntegrationTest.java
│   ├── AvatarControllerIntegrationTest.java
│   ├── MissaoControllerIntegrationTest.java
│   └── UsuarioControllerIntegrationTest.java
└── service/                         # Testes unitários (Mockito)
    ├── AuthServiceImplTest.java
    ├── AvatarServiceImplTest.java
    ├── MissaoServiceImplTest.java
    └── UsuarioServiceImplTest.java
```

---
