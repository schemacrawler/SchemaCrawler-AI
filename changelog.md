# SchemaCrawler AI Change History

SchemaCrawler AI release notes.

<a name="v16.27.1-1"></a>
## Release 16.27.1-1 - 2025-08-25

- Show objects referenced by routines
- Add lint function to MCP server (and remove it from 'aichat')
- Add table-sample function to MCP server
- Show remarks and data types wherever possible
- Add function for getting database server information
- Create a new connection from the pool for every tool execution


<a name="v16.26.3-4"></a>
## Release 16.26.3-4 - 2025-08-04

- Use later version of Spring AI and Spring Boot
- Refactor health endpoint to show uptime


<a name="v16.26.3-3"></a>
## Release 16.26.3-3 - 2025-07-15

- Make `--transport` required, with no defaults
- Fix shell script for Docker MCP Catalog
- Add logging for stdio transport


<a name="v16.26.3-4"></a>
## Release 16.26.3-2 - 2025-07-15

- Add additional tests, and fix startup issues


<a name="v16.26.3-1"></a>
## Release 16.26.3-1 - 2025-07-14

- Move compact catalog to SchemaCrawler AI
- Update copyright notices and license to EPL 2.0
- Add a new `--transport` command-line switch, and default to `stdio`
- Add a new "schemacrawler-aichat" module to further organize code
- Add new Docker image endpoint to allow start from Docker MCP Registry
