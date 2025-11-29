/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools;

import java.util.function.Supplier;

public sealed interface FunctionReturn extends Supplier<String>
    permits NoResultsFunctionReturn,
        TextFunctionReturn,
        JsonFunctionReturn,
        ExceptionFunctionReturn {}
