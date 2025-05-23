/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.aichat.mcp.server;

/**
 * Thread-safe singleton configuration manager for SchemaCrawler AI.
 * Manages configuration settings like isDryRun.
 */
public class ConfigurationManager {

    private static final Object lock = new Object();
    private static volatile ConfigurationManager instance;

    private boolean isDryRun = false;

    private ConfigurationManager() {
        // Private constructor to prevent direct instantiation
    }

    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }

    public boolean isDryRun() {
        return isDryRun;
    }

    public void setDryRun(final boolean dryRun) {
        this.isDryRun = dryRun;
    }
}
