# MedFEMA

Primeira release do sistema de gestão da clínica MedFEMA. O projeto contém uma API segura em Spring Boot e uma interface responsiva em Angular.

## Tecnologias

- Java 21, Spring Boot 3, Maven, Spring Data JPA e Bean Validation
- Spring Security com JWT
- MySQL, Hibernate e Flyway
- OpenAPI 3 / Swagger UI
- JUnit, Mockito e H2 para testes
- Angular e TypeScript
- Docker Compose para o banco de dados

## Funcionalidades

- CRUD de médicos com exclusão lógica, paginação e ordenação por nome
- CRUD de pacientes com exclusão lógica, paginação e ordenação por nome
- Agendamento e cancelamento de consultas com as regras da primeira release
- Escolha automática de um médico disponível por especialidade
- Autenticação JWT e documentação interativa da API

## Executar no VS Code

1. Abra esta pasta no VS Code.
2. Pressione `Ctrl+Shift+P` e escolha **Tasks: Run Task**.
3. Execute **MedFEMA: executar tudo**.
4. Abra `http://localhost:8080`.

Por padrão, essa tarefa inicia somente o backend, que também entrega a versão pronta do frontend. Assim o sistema abre mais rápido, em um único terminal, usando o banco H2 persistente em `backend/data`. Para alterar o frontend com atualização automática, execute a tarefa **MedFEMA: desenvolvimento** e abra `http://localhost:4200`.

O projeto também está completamente configurado para MySQL com Docker Compose.

Credenciais iniciais da aplicação:

- Usuário: `admin`
- Senha: `admin123`

## Execução manual

```powershell
cd backend
$env:SPRING_PROFILES_ACTIVE="local"
.\mvnw.cmd spring-boot:run
```

Em outro terminal:

```powershell
cd frontend
npm.cmd start
```

## Endereços

- Aplicação (modo rápido): http://localhost:8080
- Frontend em desenvolvimento: http://localhost:4200
- API: http://localhost:8080/api
- Swagger: http://localhost:8080/swagger-ui.html

## Testes e build

```powershell
cd backend
.\mvnw.cmd test
.\mvnw.cmd package

cd ..\frontend
npm.cmd run build
```

Configurações sensíveis podem ser substituídas pelas variáveis `DB_URL`, `DB_USER`, `DB_PASSWORD`, `APP_USER`, `APP_PASSWORD`, `JWT_SECRET` e `CORS_ORIGIN`.

Para executar com MySQL, habilite o WSL 2 no Windows, abra o Docker Desktop, execute `docker compose up -d` na raiz e inicie o backend sem o perfil `local`.

## Publicação na internet

O repositório contém dois projetos:

- `frontend`: aplicação Angular pronta para a Vercel.
- `backend`: API Spring Boot pronta para um serviço que execute Java ou Docker.

A Vercel não oferece runtime Java oficial. O arquivo `render.yaml` publica a API e um PostgreSQL no Render, enquanto a Vercel hospeda o frontend. O banco gratuito do Render é indicado para demonstração e possui prazo de expiração definido pelo provedor.

Na Vercel:

1. Importe este repositório do GitHub.
2. Configure **Root Directory** como `frontend`.
3. Mantenha o preset **Angular**.
4. Crie a variável `MEDFEMA_API_URL` com o endereço público do backend terminado em `/api`.
5. No backend, configure `CORS_ORIGIN` com o domínio fornecido pela Vercel.
6. Faça o deploy.

O frontend possui configuração específica para a Vercel em `frontend/vercel.json`. A interface foi adaptada para computador, tablet e celular, incluindo navegação compacta, formulários em uma coluna e agenda em cartões nas telas pequenas.
