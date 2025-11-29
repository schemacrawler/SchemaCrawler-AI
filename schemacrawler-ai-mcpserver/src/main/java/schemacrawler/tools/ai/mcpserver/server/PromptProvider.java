/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import java.util.List;
import org.springaicommunity.mcp.annotation.McpPrompt;
import org.springframework.stereotype.Service;

@Service
public class PromptProvider {

  @McpPrompt(name = "database-expert", description = "Behave like a database expert")
  public GetPromptResult databaseExpert() {
    final String message =
        """
        You are a relational database expert focused on fast, accurate schema understanding.
        Use the SchemaCrawler MCP Server tools to discover schemas, tables, columns, keys,
        indexes, and routines, then produce clear, structured answers. Keep responses concise
        and skimmable, prefer bullet points, and avoid long prose. Only include facts retrieved
        from the tools or stated by the user.
        """
                .stripIndent()
                .replace("\n", " ")
                .trim()
            + "\n\n";
    return new GetPromptResult(
        "Database expert prompt", List.of(new PromptMessage(Role.USER, new TextContent(message))));
  }

  @McpPrompt(name = "sql-query-assistant", description = "Provide help writing SQL queries")
  public GetPromptResult sqlQueryAssistant() {
    final String message =
        """
        You are an expert SQL developer focused on helping users write correct, efficient
        queries. Use SchemaCrawler MCP tools to understand the database schema, then provide
        accurate SQL solutions with proper table joins, column references, and query patterns.
        Always validate schema details before suggesting queries. Write SQL using the exact
        column names and data types from the schema. Write SQL using the exact column names and
        data types from the schema. Include proper JOIN syntax based on discovered foreign key
        relationships. Add appropriate WHERE clauses, ORDER BY, and aggregations as requested.
        Use schema-qualified names (schema.table) when multiple schemas exist.
        """
                .stripIndent()
                .replace("\n", " ")
                .trim()
            + "\n\n";
    return new GetPromptResult(
        "SQL query assistant prompt",
        List.of(new PromptMessage(Role.USER, new TextContent(message))));
  }
}
