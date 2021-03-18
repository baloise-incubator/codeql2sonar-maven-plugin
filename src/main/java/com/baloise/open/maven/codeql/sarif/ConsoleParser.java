package com.baloise.open.maven.codeql.sarif;

import com.baloise.open.maven.codeql.sarif.dto.Driver;
import com.baloise.open.maven.codeql.sarif.dto.Result;
import com.baloise.open.maven.codeql.sarif.dto.Rule;
import org.apache.maven.plugin.logging.Log;

public class ConsoleParser implements ParserCallback {

  private final Log logger;

  public ConsoleParser(Log logger) {
    this.logger = logger;
  }

  @Override
  public void onFinding(Result result) {
    logger.info(result.toString());
  }

  @Override
  public void onVersion(String version) {
    logger.info("Sarif version: " + version);
  }

  @Override
  public void onSchema(String schema) {
    logger.info("Sarif schema: " + schema);
  }

  @Override
  public void onDriver(Driver driver) {
    logger.info("Driver: " + driver);
  }

  @Override
  public void onRule(Rule rule) {
    logger.info(String.format("Processed rule[%s]: %s", rule.getId(), rule.getName() ));
  }
}
