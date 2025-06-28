# GEMINI.md

## Contexto do Projeto
Este é um projeto de blog/rede social construído com Spring Boot 3.x, Java 21, e Domain-Driven Design (DDD). Inclui autenticação de usuários, criação de postagens, comentários, e upload de imagens. Usa Spring Security com OAuth2/JWT, banco H2 para desenvolvimento, e Maven para gerenciamento de dependências.

### Estrutura do Projeto
- Pacotes:
    - `com.devluan.blog_api.domain`: Entidades e Value Objects.
    - `com.devluan.blog_api.application`: Serviços de aplicação.
    - `com.devluan.blog_api.infrastructure`: Repositórios e configurações de persistência.
- Entidades principais: `User`, `Post`, `Comment`.
- Banco de dados: H2 (desenvolvimento), com possibilidade de PostgreSQL em produção.

## System Prompts

1. **Geração de Código**:
    - Gere código em Java 21, compatível com Spring Boot 3.x.
    - Siga DDD: separe lógica de domínio (entidades, value objects) de serviços e repositórios.
    - Use nomes descritivos e siga a estrutura de pacotes: `com.devluan.blog_api`.
    - Inclua tratamento de exceções (e.g., `NotFoundException`, `DuplicateEmailException`).
    - Para endpoints REST, use `@RestController`, `@RequestMapping`, e retorne `ResponseEntity` com status HTTP apropriados.

2. **Linguagem**:
    - Responda em português brasileiro.
    - Seja conciso, mas inclua explicações claras quando solicitado.

3. **Modificação de Arquivos**:
    - Proponha alterações em branches separadas (e.g., `gemini-<nome-da-funcionalidade>`).
    - Mostre diffs e aguarde confirmação do usuário antes de salvar.
    - Respeite o estilo de código existente (2 espaços para indentação, camelCase).

4. **Testes**:
    - Gere testes unitários com JUnit 5 e Mockito para serviços e controllers.
    - Cubra casos de uso comuns e cenários de erro (e.g., entrada inválida, recurso não encontrado).

5. **Funcionalidades Específicas**:
    - **Autenticação**: Use Spring Security com OAuth2 e JWT para endpoints `/auth/register` e `/auth/login`.
    - **Postagens**: Suporte texto e upload de imagens (`MultipartFile`) com validação de formato (JPEG, PNG) e tamanho (máx. 5MB).
    - **Comentários**: Use JPA com `@ManyToOne` para relacionar comentários a postagens.
    - **Cache**: Use Spring Cache com Caffeine para consultas frequentes (e.g., `existsByEmail`).

## Estilo de Código
- Indentação: 2 espaços.
- Nomenclatura: camelCase para métodos e variáveis, PascalCase para classes.
- Use `@Validated` e `@Valid` para validação de entrada em controllers.
- Injete dependências via construtor.
- Inclua Javadoc para métodos públicos em serviços e controllers.

## Exemplo de Prompt
- Para criar um endpoint: "Adicione um endpoint REST para criar postagens em `PostController.java`."
- Para testes: "Crie testes unitários para o serviço de criação de postagens."
- Para upload de imagens: "Adicione suporte para upload de imagens em postagens, validando formato e tamanho."

## Integração com Spring AI
- Use `spring-ai-vertex-ai-gemini` para geração de conteúdo (e.g., resumos de postagens).
- Exemplo: "Gere um resumo de um post com base no texto fornecido."