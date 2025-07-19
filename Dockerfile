# ========================================================================
# SchemaCrawler AI
# http://www.schemacrawler.com
# Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
# All rights reserved.
# SPDX-License-Identifier: EPL-2.0
# ========================================================================

# Builder stage - Build SchemaCrawler AI
FROM maven:3.9-eclipse-temurin-21 AS builder

# Copy source code
COPY . .

# Build SchemaCrawler AI (as per quick-build.yml)
RUN mvn \
      --no-transfer-progress \
      --batch-mode \
      clean package

# Production stage - Use SchemaCrawler base image
ARG FROM_IMAGE=schemacrawler/schemacrawler:latest
FROM ${FROM_IMAGE}

# Copy SchemaCrawler AI distribution from builder stage
# Based on the upload artifact path in quick-build.yml
COPY --from=builder \
  /build/_aichat-distrib/ \
  /opt/schemacrawler/

CMD ["bash", "/opt/schemacrawler/bin/schemacrawler-ai.sh"]
