package third.sl4j;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;
import java.io.File;
import org.slf4j.LoggerFactory;

public class Sl4jOnly {
  public static void main(String[] args) {
    org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Sl4jOnly.class);
    logger.info("Hello, SLF4J!");
  }

  public static void load(String path) {
    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    try {
      File file = new File(path);
      if (!file.exists() || !file.isFile() || !file.canRead()) {
        return;
      }
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.setContext(lc);
      lc.reset();
      configurator.doConfigure(file);
    } catch (Exception e) {
      System.err.println("Failed to load logback config from: " + path);
      e.printStackTrace();
    } finally {
      StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
    }
  }
}
