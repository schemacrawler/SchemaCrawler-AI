# SchemaCrawler AI Change History

SchemaCrawler AI release notes.

<a name="v17.6.0-2"></a>
## Release 17.6.0-2 - 2026-01-27

- Upgrade to Spring AI 2.0.0-M2


<a name="v17.6.0-1"></a>
## Release 17.6.0-1 - 2026-01-16

- Build against SchemaCrawler v17.6.0


<a name="v17.5.0-1"></a>
## Release 17.5.0-1 - 2026-01-16

- Add entity information


<a name="v17.4.0-1"></a>
## Release 17.4.0-1 - 2026-01-16

- Fix Docker image by reverting back to Spring AI 1
- Add a test to ensure that the Docker image works, by running the image in Testcontainers
- Add ER modeling information - entity types and foreign key cardinality - to schema information returned by functions


<a name="v17.1.7-3"></a>
## Release 17.1.7-3 - 2025-12-12

- Remove playground code


<a name="v17.1.7-2"></a>
## Release 17.1.7-2 - 2025-12-12

- Update to Spring AI 2 (which uses Spring Boot 4)


<a name="v17.1.7-1"></a>
## Release 17.1.7-1 - 2025-11-29

- Update Jackson patch version
- Update copyright year to 2026


<a name="v17.1.6-2"></a>
## Release 17.1.6-2 - 2025-11-23

- Add ability to generate Graphviz diagrams


<a name="v17.1.6-1"></a>
## Release 17.1.6-1 - 2025-11-20

- Support Jackson 3


<a name="v17.1.5-3"></a>
## Release 17.1.5-3 - 2025-11-16

- Update to use Spring AI 1.1.0 GA


<a name="v17.1.5-2"></a>
## Release 17.1.5-2 - 2025-11-14

- Add tool to generate database diagrams
- Update tool descriptions


<a name="v17.1.5-1"></a>
## Release 17.1.5-1 - 2025-11-11

- Update Spring AI dependencies


<a name="v17.1.4-1"></a>
## Release 17.1.4-1 - 2025-11-02

- Build against latest SchemaCrawler version
- Allow tools to be excluded


<a name="16.29.1-1"></a>
## Release 16.29.1-1 - 2025-09-29

- Remove "aichat" command


<a name="v16.28.2-2"></a>
## Release 16.28.2-2 - 2025-09-25

- Add prompts


<a name="v16.28.2-1"></a>
## Release 16.28.2-1 - 2025-09-19

- Support streamable HTTP protocol for MCP Server
- Publish MCP resources for database object metadata


<a name="v16.28.1-2"></a>
## Release 16.28.2-2 - 2025-09-17

- Add tags to publish to https://github.com/modelcontextprotocol/registry/


<a name="v16.28.1-1"></a>
## Release 16.28.2-2 - 2025-09-16

- Refactor code to make it more idiomatic in the Spring Frameork
- Format tool error messages in JSON


<a name="v16.27.2-1"></a>
## Release 16.27.2-1 - 2025-09-03

- Allow MCP Server to be started entirely from environmental variables
- Start MCP Server in an error state if a database connection cannot be made


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
