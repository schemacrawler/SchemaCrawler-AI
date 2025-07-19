# ========================================================================
# SchemaCrawler AI
# http://www.schemacrawler.com
# Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
# All rights reserved.
# SPDX-License-Identifier: EPL-2.0
# ========================================================================

ARG FROM_IMAGE=schemacrawler/schemacrawler:latest
FROM ${FROM_IMAGE}

# Copy SchemaCrawler AI extra distribution jars from the build directory
COPY \
    ./extra-lib/* /opt/schemacrawler/lib
COPY \
    ./extra-bin/* /opt/schemacrawler/bin

CMD ["bash", "/opt/schemacrawler/bin/schemacrawler-ai.sh"]
