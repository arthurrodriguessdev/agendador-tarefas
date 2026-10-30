# Agendador de Tarefas

API REST para criar e gerenciar tarefas agendadas. O serviço autentica as requisições com JWT, persiste as tarefas no PostgreSQL e publica eventos no Kafka para consumo pelo serviço Notificador.

## Arquitetura

```text
Cliente
  |
  | HTTP + JWT
  v
API Agendador de Tarefas ------> Serviço de Usuários
  |                              valida/consulta o usuário
  |
  +------> PostgreSQL
  |        tabela tarefa
  |        tarefas e e-mail do proprietário
  |
  +------> Kafka ------> Serviço Notificador (consumidor externo)
           created-task   recebe tarefas criadas
           next-task      recebe tarefas pendentes próximas
```

O e-mail do proprietário é obtido a partir do token, não do corpo enviado pelo cliente. As tarefas são armazenadas no PostgreSQL e associadas a esse e-mail. Ao criar uma tarefa, a aplicação solicita a publicação do evento `created-task`. A rota de notificação busca tarefas pendentes previstas para a próxima hora e publica cada uma no tópico `next-task`.

O serviço Notificador é externo a este repositório. Ele deve consumir os tópicos do Kafka e tratar os eventos recebidos; este projeto não implementa o consumidor nem o envio de notificações.

## Tecnologias

- Java 21 e Spring Boot
- Spring MVC, Spring Security, Spring Data JPA e OpenFeign
- PostgreSQL
- Apache Kafka
- Gradle

## Configuração

Configure as variáveis de ambiente antes de iniciar a aplicação:

| Variável | Finalidade |
| --- | --- |
| `SPRING_DATASOURCE_URL_AGENDADOR` | URL JDBC do PostgreSQL |
| `SPRING_DATASOURCE_USERNAME_AGENDADOR` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD_AGENDADOR` | Senha do banco |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Endereço dos brokers Kafka |
| `USUARIO_URL` | URL base do serviço de Usuários |
| `SECRET_KEY_JWT` | Chave Base64 usada para validar os tokens JWT |
| `SERVER_PORT` | Porta HTTP da API |
| `JPA_DDL_AUTO` | Estratégia de criação/atualização do esquema |
| `JPA_SHOW_SQL` | Habilita a exibição de SQL nos logs |

Use credenciais e uma chave JWT seguras fora do ambiente local. Para produção, prefira migrações versionadas do banco em vez de `ddl-auto=update`.

## API

Todas as rotas exigem o cabeçalho `Authorization: Bearer <token>`.

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/tarefas` | Cria uma tarefa, grava no banco e publica `created-task`; retorna `201` |
| `GET` | `/tarefas` | Lista as tarefas do usuário autenticado |
| `GET` | `/tarefas/eventos?dataInicio=2026-10-25T00:00:00&dataFim=2026-10-26T00:00:00` | Lista tarefas do usuário no período informado |
| `PUT` | `/tarefas/{id}` | Atualiza os campos não nulos enviados para a tarefa |
| `PATCH` | `/tarefas/{id}?status=PENDENTE` | Altera o status para `PENDENTE`, `NOTIFICADO` ou `CANCELADO` |
| `DELETE` | `/tarefas/{id}` | Exclui uma tarefa do usuário autenticado |
| `POST` | `/tarefas/proximas/notificar` | Publica em `next-task` as tarefas pendentes previstas para a próxima hora |

As datas dos parâmetros de consulta usam o formato ISO de data e hora. No corpo JSON, os campos de data usam `dd-MM-yyyy HH:mm:ss`.