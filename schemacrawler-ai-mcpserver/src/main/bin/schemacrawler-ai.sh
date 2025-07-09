#!/usr/bin/env bash
SC_DIR=/opt/schemacrawler
java -cp "$SC_DIR"/lib/*:"$SC_DIR"/config schemacrawler.tools.command.aichat.mcp.DockerMcpServer "$@"
