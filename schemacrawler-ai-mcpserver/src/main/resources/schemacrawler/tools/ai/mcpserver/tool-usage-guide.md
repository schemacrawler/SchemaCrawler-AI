# Tools Usage Guide for SchemaCrawler AI MCP Server

1. **Orient.** Use `about_database` to learn the database engine, version and settings before writing platform-specific SQL. When you do not know table names yet, use `table_importance` to find the key tables, and `detect_clusters` to find the functional areas of the schema.

2. **Discover.** Start here for most questions. Use `list` for an inventory of tables, views, routines, sequences and synonyms, filtered by object type and name pattern. Use `list_members_of_tables` to search for columns, indexes, foreign keys or triggers across tables, for example every column that matches "email".

3. **Investigate.** Use the fully qualified names returned by earlier tools. Use `describe_tables` for columns, keys, indexes, triggers and DDL; `describe_routines` for parameters, return types and DDL; `describe_er_relationships` for conceptual one-to-one, one-to-many and many-to-many relationships. Use `table_path` for the shortest join path between two tables, which can be useful to generate SQL.

4. **Assess.** Use `lint` for design and naming problems, `table_sample` for a few random rows of real data, and `diagram` for a picture of the schema, narrowed to specific tables first.

Use regular expression filters to keep results small.
