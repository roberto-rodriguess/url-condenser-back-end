# URL Condenser API 🔗

API REST de encurtamento de URLs segura, rápida e escalável, desenvolvida com **Java**, **Spring Boot 3.x** e **PostgreSQL**. O sistema conta com autenticação JWT, controle de acesso a links por usuário, logs de acessos em tempo real e compilação nativa com **GraalVM** pronta para conteinerização e deploy na nuvem.

---

## 🛠️ Tecnologias Utilizadas

<div align="left">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white">
  <img src="https://img.shields.io/badge/GraalVM-Native_Image-FE354B?style=for-the-badge&logo=graalvm&logoColor=white">
  <img src="https://img.shields.io/badge/Flyway-CC0000?style=for-the-badge&logo=redhat&logoColor=white">
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black">
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
</div>

---

## 🚀 Funcionalidades Principais

- 👤 **Autenticação Segura:** Registro de usuários e login com geração de tokens JWT.
- 🔗 **Encurtamento Inteligente:** Geração de códigos curtos de redirecionamento.
- 🔒 **Controle de Dono:** Usuários autenticados gerenciam apenas suas próprias URLs.
- 📊 **Estatísticas de Acesso:** Contagem automatizada de cliques e acessos.
- 🔄 **Redirecionamento Rápido:** Redirecionamento HTTP 302 com tratamento para links inválidos integrado ao frontend.
- 📚 **Documentação Interativa:** OpenAPI/Swagger integrado para testes rápidos.
- ⚡ **Compilação Nativa:** Suporte total a builds GraalVM (inicialização instantânea e consumo mínimo de memória RAM).

---

## 🗂️ Estrutura do Projeto

```text
url-condenser
│
├── .github/workflows/   # Pipeline unificado de CI/CD (GitHub Actions)
├── src/
│   ├── main/
│   │   ├── java/com/matrob/urlcondenser/
│   │   │   ├── url/        # Domínio, Serviços e Endpoints de URLs
│   │   │   ├── usuario/    # Registro, Login e Perfis de Usuários
│   │   │   ├── infra/      # Tratamento global de erros e exceções
│   │   │   └── security/   # Configuração de segurança e lógica JWT
│   │   └── resources/
│   │       ├── db/migration/  # Migrations do Flyway (PostgreSQL)
│   │       └── application.properties
│   └── test/               # Testes automatizados (H2 em memória)
│
├── Dockerfile           # Imagem enxuta baseada em Debian
├── pom.xml              # Gerenciador de Dependências Maven
└── README.md
```

---

## 📡 Endpoints da API

### Autenticação (Pública)
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/register` | Registra um novo usuário no sistema |
| `POST` | `/login` | Autentica um usuário e retorna o Token JWT |

### Gerenciamento de URLs (Requer Cabeçalho `Authorization: Bearer <TOKEN>`)
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/api/urls` | Encurta uma URL original |
| `GET` | `/api/urls` | Lista todas as URLs cadastradas pelo usuário logado |
| `GET` | `/api/urls/{id}` | Busca os detalhes de uma URL pelo seu ID numérico |
| `GET` | `/api/urls/stats/{shortCode}` | Consulta estatísticas detalhadas de acessos |
| `DELETE` | `/api/urls/{id}` | Exclui de forma permanente uma URL cadastrada |

### Redirecionamento (Público)
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/{shortCode}` | Redireciona o visitante para a URL original |

---

## 📚 Documentação e Testes

Após iniciar a aplicação localmente, você pode conferir e testar todos os endpoints interativamente através do Swagger UI em:
```text
http://localhost:8080/swagger-ui/index.html
```

---

## 🛠️ Requisitos de Sistema

- **Java Development Kit (JDK):** Versão 21 ou superior (preferencialmente GraalVM para builds nativos).
- **Gerenciador de Build:** Maven 3.9+.
- **Banco de Dados:** PostgreSQL 15+ (para desenvolvimento e produção) e banco H2 em memória (para testes automatizados).

---

## 💻 Como Executar Localmente

### 1. Clonar o repositório
```bash
git clone https://github.com/G0LDB3G/url-condenser.git
cd url-condenser
```

### 2. Configurar as Variáveis de Ambiente
Crie as seguintes variáveis de ambiente na sua máquina ou configure diretamente no seu `application.properties`:
* `SPRING_DATASOURCE_URL` (ex: `jdbc:postgresql://localhost:5432/urlcondenser`)
* `SPRING_DATASOURCE_USERNAME` (ex: `postgres`)
* `SPRING_DATASOURCE_PASSWORD` (ex: `sua_senha`)
* `API_SECURITY_TOKEN_SECRET` (chave secreta para assinatura dos tokens JWT)
* `FRONTEND_URL` (URL da aplicação frontend externa para onde links inválidos serão redirecionados)

### 3. Rodar em Modo de Desenvolvimento
```bash
mvn spring-boot:run
```

### 4. Rodar os Testes Automatizados (H2)
```bash
mvn clean test
```

---

## 📦 Compilação Nativa e CI/CD

Este projeto está pronto para ambientes de alta performance utilizando GraalVM Native Image:

* **Compilação Local Windows (VS Native Tools Command Prompt):**
  ```bash
  mvn -Pnative native:compile -DskipTests "-Dnative.buildargs=-H:DeadlockWatchdogInterval=0"
  ```
* **Esteira Integrada GitHub Actions:**
  - **Pull Requests:** Roda testes automatizados de segurança.
  - **Pushes na branch `main`:** Faz o deploy do container Linux direto para o **Railway**.
  - **Pushes de Tags (`v*`):** Roda os testes, compila o binário `.exe` para Windows nativo e lança uma **Release** com o arquivo anexado.
