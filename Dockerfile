# ========================================================================
# SchemaCrawler AI
# http://www.schemacrawler.com
# Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
# All rights reserved.
# SPDX-License-Identifier: CC-BY-NC-4.0
# ========================================================================

# Provided arguments
ARG FROM_IMAGE=schemacrawler/schemacrawler:v16.28.1

# BUILDER stage - Build SchemaCrawler AI
FROM maven:3.9-eclipse-temurin-21 AS builder

# Copy source code
COPY . .

# Build SchemaCrawler AI (as per quick-build.yml)
RUN \
  mvn \
    --no-transfer-progress \
    --batch-mode \
    clean package


# PRODUCTION stage - Create SchemaCrawler AI image
FROM ${FROM_IMAGE}

# Copy SchemaCrawler AI distribution from builder stage
COPY --from=builder \
  ./_ai-distrib/ \
  /opt/schemacrawler/

CMD ["bash", "/opt/schemacrawler/bin/schemacrawler-ai.sh"]
