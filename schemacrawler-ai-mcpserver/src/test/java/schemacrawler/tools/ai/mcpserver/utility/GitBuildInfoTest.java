/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.utility;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class GitBuildInfoTest {

  @Test
  void rendersTrimmedCommitInformation() {
    final GitBuildInfo info = new GitBuildInfo("  abc123  ", "  2026-11-02T00:00:00Z  ");

    assertThat(info.isEmpty(), is(false));
    assertThat(
        info.toString(),
        is(
            "Git information:  Commit SHA:abc123"
                + System.lineSeparator()
                + "  Commit time:2026-11-02T00:00:00Z"
                + System.lineSeparator()));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\t\r\n"})
  void missingCommitTimeIsOmitted(final String commitTime) {
    final GitBuildInfo info = new GitBuildInfo("abc123", commitTime);

    assertThat(info.isEmpty(), is(false));
    assertThat(info.toString(), is("Git information:  Commit SHA:abc123" + System.lineSeparator()));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\t\r\n"})
  void missingCommitIdIsEmptyEvenWithCommitTime(final String commitId) {
    final GitBuildInfo info = new GitBuildInfo(commitId, "2026-11-02T00:00:00Z");

    assertThat(info.isEmpty(), is(true));
    assertThat(
        info.toString(),
        is("Git information:  Commit time:2026-11-02T00:00:00Z" + System.lineSeparator()));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\t\r\n"})
  void missingMetadataRendersNothing(final String value) {
    final GitBuildInfo info = new GitBuildInfo(value, value);

    assertThat(info.isEmpty(), is(true));
    assertThat(info.toString(), is(""));
  }
}
