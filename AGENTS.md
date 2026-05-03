# AGENTS.md — SchemaCrawler-AI

SchemaCrawler-AI exposes SchemaCrawler's database schema discovery as an **MCP (Model Context Protocol) Server**, making database introspection available as tools for AI agents and LLMs. It provides a function plugin library (`schemacrawler-ai-core`) and a Spring Boot MCP server (`schemacrawler-ai-mcpserver`).

**License:** Business Source License 1.1 (BUSL-1.1) — non-production use only without a separate commercial agreement.

## Build and Test

```bash
# Standard build and unit tests (Java 21 required)
mvn clean verify

# With Docker-based image verification (requires Docker)
mvn clean verify -Dverify
```

## Module Layout

| Module | Purpose |
|--------|---------|
| `schemacrawler-ai-core` | `FunctionDefinition` plugin system and 10 built-in database introspection functions |
| `schemacrawler-ai-mcpserver` | Spring Boot MCP server that exposes functions as MCP tools, resources, and prompts |
| `schemacrawler-ai-distrib` | Assembles the distribution lib/bin packages |
| `schemacrawler-ai-verify` | Docker image verification tests — activated by `-Dverify` |

## Architecture

### Function Definition Pattern (`schemacrawler-ai-core`)

Each AI-callable function is a trio of classes in `schemacrawler.tools.ai.functions`:

```
{Name}FunctionDefinition   — metadata, JSON schema declaration, creates the executor
{Name}FunctionParameters   — input parameter model (drives JSON schema generation)
{Name}FunctionExecutor     — execution logic against a SchemaCrawler Catalog
```

Base class: `AbstractFunctionDefinition<P>` (template method pattern).
Registration: `FunctionDefinitionRegistry` uses `ServiceLoader` — add the class name to `META-INF/services/schemacrawler.tools.ai.tools.FunctionDefinition`.

Built-in functions: `DescribeEntities`, `DescribeRelationships`, `DescribeRoutines`, `DescribeTables`, `Diagram`, `Lint`, `ListAcrossTablesFunctions`, `ListFunctions`, `ServerInformation`, `TableSample`.

Function names are converted to `snake_case` for MCP protocol compatibility.

### MCP Server (`schemacrawler-ai-mcpserver`)

Built with **Spring Boot 4** and **Spring AI 2.0.0-M5** (`spring-ai-starter-mcp-server-webmvc`).

| Class | Role |
|-------|------|
| `McpServerMain` | `@SpringBootApplication` entry point |
| `McpServerInitializer` | Application context initialization with error-state handling |
| `McpServerContext` | Parses MCP transport type and tool exclusion list from env vars |
| `SchemaCrawlerContext` | Builds database connection and SchemaCrawler options from env vars |
| `ToolProvider` | Registers MCP tools via `@McpTool` annotations |
| `ResourceProvider` | Exposes MCP resources (schema snapshots, etc.) |
| `PromptProvider` | Provides MCP prompt templates |
| `DatabaseConnectionService` | Manages JDBC connection lifecycle |

Transport modes: **stdio** (default) or **HTTP** — controlled by `SCHCRWLR_MCP_TRANSPORT`.

When the database connection cannot be established, `InErrorFactory` provides a fallback context that returns a descriptive error message from every tool instead of crashing the server.

### Configuration (Environment Variables)

| Variable | Purpose |
|----------|---------|
| `SCHCRWLR_JDBC_URL` | Full JDBC URL (mutually exclusive with the four fields below) |
| `SCHCRWLR_SERVER` | Database server type (e.g., `postgresql`, `mysql`, `sqlite`) |
| `SCHCRWLR_HOST` | Database hostname |
| `SCHCRWLR_PORT` | Database port |
| `SCHCRWLR_DATABASE` | Database name |
| `SCHCRWLR_MCP_TRANSPORT` | `stdio` (default) or `http` |
| `SCHCRWLR_EXCLUDED_TOOLS` | Comma-separated tool names to exclude from registration |

## Coding Guidelines

- Java 21; use modern language features (records, sealed classes, text blocks, pattern matching) where appropriate.
- Follow the same immutability and thread-safety conventions as SchemaCrawler-Core: use `final` on fields, parameters, and local variables; avoid mutable shared state.
- Tests use **JUnit 5** with **Hamcrest** matchers; integration tests use the shared HSQLDB test database from `schemacrawler-testdb`.
- All dependency versions are inherited from `schemacrawler-parent`; do not declare versions in sub-module POMs.
