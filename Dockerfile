# ========================================================================
# SchemaCrawler AI
# http://www.schemacrawler.com
# Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
# All rights reserved.
# SPDX-License-Identifier: EPL-2.0
# ========================================================================

# Provided arguments
ARG FROM_IMAGE=schemacrawler/schemacrawler:latest

# Builder stage - Build SchemaCrawler AI
FROM maven:3.9-eclipse-temurin-21 AS builder

# Copy source code
COPY . .

# Build SchemaCrawler AI (as per quick-build.yml)
RUN \
  mvn \
    --no-transfer-progress \
    --batch-mode \
    clean package

# DEBUG
RUN \
   pwd \
&& ls -1 


# Production stage - Use SchemaCrawler base image
FROM ${FROM_IMAGE}

# Copy SchemaCrawler AI distribution from builder stage
COPY --from=builder \
  ./_aichat-distrib/ \
  /opt/schemacrawler/

CMD ["bash", "/opt/schemacrawler/bin/schemacrawler-ai.sh"]
