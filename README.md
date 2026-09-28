# Studio AS API

API REST do Studio AS para automação e gestão de leads. O projeto está atualmente na etapa de estruturação: controllers, serviços e integrações estão criados como scaffolds, mas ainda não possuem endpoints ou regras de negócio implementados.

## Tecnologias

- Java 21
- Spring Boot 3.5.6
- Spring Web, Spring Data JPA, Spring Security e Spring Actuator
- PostgreSQL
- springdoc OpenAPI / Swagger UI
- Maven

## Pré-requisitos

- JDK 21
- Maven 3.6.3 ou superior
- PostgreSQL em execução

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

Altere-as conforme a configuração do seu PostgreSQL. Não use a senha padrão em ambientes compartilhados ou de produção.

No PowerShell, por exemplo:

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

A API usa a porta `8080`. O Hibernate está configurado com `ddl-auto: update`, portanto atualiza o esquema do banco automaticamente ao iniciar.

## Endpoints disponíveis

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Health check: <http://localhost:8080/actuator/health>
- Informações do Actuator: <http://localhost:8080/actuator/info>

O `spring-boot-starter-security` está presente e ainda não há uma política de segurança personalizada. Assim, o Spring Security aplica sua configuração padrão e pode exigir autenticação, com a senha gerada registrada no log de inicialização. Os endpoints de negócio ainda serão definidos.

## Organização do código

O código fica em `src/main/java/com/studioas/api`, dividido em `config`, `controller`, `service`, `repository`, `model`, `dto`, `integration` e `exception`. A configuração Spring está em `src/main/resources/application.yml`.
