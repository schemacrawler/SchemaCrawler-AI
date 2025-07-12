#!/usr/bin/env bash
# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: CC-BY-NC-4.0

SC_DIR=/opt/schemacrawler
java -cp "$SC_DIR"/lib/*:"$SC_DIR"/config schemacrawler.tools.command.aichat.mcp.DockerMcpServer "$@"
