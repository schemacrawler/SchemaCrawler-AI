package schemacrawler.tools.command.aichat.mcp.server;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import schemacrawler.Version;
import us.fatehi.utility.string.StringFormat;

@Component
@EnableScheduling
public class HeartbeatLogger {

  private static final Logger LOGGER = Logger.getLogger(HeartbeatLogger.class.getCanonicalName());

  private final Environment environment;

  public HeartbeatLogger(final Environment environment) {
    this.environment = environment;
  }

  @Scheduled(timeUnit = TimeUnit.SECONDS, fixedRate = 30)
  public void logHeartbeat() {
    final boolean heartbeat = Boolean.valueOf(environment.getProperty("server.heartbeat", "false"));
    if (!heartbeat) {
      return;
    }

    final String serverName = environment.getProperty("server.name", "SchemaCrawler MCP Server");
    final String serverVersion = Version.version().toString();
    final long uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1_000;

    LOGGER.log(
        Level.INFO,
        new StringFormat(
            "Heartbeat: %s (%s) is running. Uptime %d seconds.",
            serverName, serverVersion, uptime));
  }
}
