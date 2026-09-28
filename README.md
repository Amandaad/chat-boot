# Studio AS API

API REST do Studio AS para automação e gestão de leads. O projeto está atualmente na etapa de estruturação: controllers, serviços e integrações estão criados como scaffolds, mas ainda não possuem endpoints ou regras de negócio implementados.

## Tecnologias

- Java 21
- Spring Boot 3.5.6
- Spring Web, Spring Data JPA, Spring Security e Spring Actuator
- PostgreSQL
- ManyChat External Request e OpenAI
- springdoc OpenAPI / Swagger UI
- Maven

## Pré-requisitos

- JDK 21
- Maven 3.6.3 ou superior
- PostgreSQL em execução para usar a configuração padrão
- H2 é usado no perfil local e não exige instalação do PostgreSQL

Crie o banco local antes de iniciar a aplicação:

```sql
CREATE DATABASE studio_as;
```

## Configuração

A aplicação usa estas variáveis de ambiente, com os valores padrão indicados:

| Variável | Padrão |
| --- | --- |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/studio_as` |
| `DATABASE_USER` | `postgres` |
| `DATABASE_PASSWORD` | `postgres` |
| `MANYCHAT_WEBHOOK_SECRET` | Sem padrão; obrigatório para o callback |
| `OPENAI_API_KEY` | Sem padrão; obrigatório para gerar respostas |
| `OPENAI_MODEL` | `gpt-4.1-mini` |
| `OPENAI_SYSTEM_PROMPT` | Instruções padrão em português para o Studio AS |

Altere-as conforme a configuração do seu PostgreSQL. Não use a senha padrão em ambientes compartilhados ou de produção.

No PowerShell, por exemplo, para configurar o PostgreSQL:

```powershell
$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/studio_as"
$env:DATABASE_USER = "postgres"
$env:DATABASE_PASSWORD = "sua-senha-local"
```

## Executar

Na raiz do projeto:

```powershell
mvn clean test
mvn spring-boot:run
```

Para executar sem PostgreSQL, use o perfil `local`, que cria um banco H2 em memória:

```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=local --server.port=8081"
```

Os dados do H2 são temporários e desaparecem quando a aplicação é encerrada. O perfil padrão continua usando PostgreSQL.

## ManyChat e respostas com IA

A API expõe `POST /webhooks/manychat/reply`. Configure no fluxo do ManyChat um bloco **External Request** com:

- URL: `https://<seu-dominio-publico>/webhooks/manychat/reply`
- Método: `POST`
- Header: `X-ManyChat-Secret` com o mesmo valor de `MANYCHAT_WEBHOOK_SECRET`
- Corpo JSON: `{"text":"{{last_text_input}}"}`

Mapeie o campo JSON `reply` da resposta para um campo personalizado do ManyChat e envie esse campo em uma mensagem do fluxo. O callback precisa estar publicado em uma URL HTTPS acessível pelo ManyChat; `localhost` só serve para testes locais. Configure `OPENAI_API_KEY` no ambiente do servidor. Nunca coloque chaves ou segredos no código ou no corpo público da requisição.

O endpoint valida o segredo compartilhado antes de chamar o OpenAI. Em produção, apenas esse callback é liberado sem autenticação Spring; os demais endpoints continuam protegidos.

A API usa a porta `8080`. O Hibernate está configurado com `ddl-auto: update`, portanto atualiza o esquema do banco automaticamente ao iniciar.

## Endpoints disponíveis

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Health check: <http://localhost:8080/actuator/health>
- Informações do Actuator: <http://localhost:8080/actuator/info>

No perfil `local`, as rotas são liberadas para facilitar o desenvolvimento. Fora dele, o callback ManyChat exige `MANYCHAT_WEBHOOK_SECRET`; os demais endpoints continuam protegidos pelo Spring Security.

## Organização do código

O código fica em `src/main/java/com/studioas/api`, dividido em `config`, `controller`, `service`, `repository`, `model`, `dto`, `integration` e `exception`. A configuração Spring está em `src/main/resources/application.yml`.
