#!/usr/bin/env bash
# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: BUSL-1.1

SC_DIR=/opt/schemacrawler
java -cp "$SC_DIR"/lib/*:"$SC_DIR"/config schemacrawler.tools.ai.mcpserver.McpServerMain "$@"
