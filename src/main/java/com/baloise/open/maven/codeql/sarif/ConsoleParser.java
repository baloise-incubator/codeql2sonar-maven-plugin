/*
 Copyright 2021 Baloise Group

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
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
    logger.debug(result.toString());
  }

  @Override
  public void onVersion(String version) {
    logger.debug("Sarif version: " + version);
  }

  @Override
  public void onSchema(String schema) {
    logger.debug("Sarif schema: " + schema);
  }

  @Override
  public void onDriver(Driver driver) {
    logger.debug("Driver: " + driver);
  }

  @Override
  public void onRule(Rule rule) {
    logger.debug(String.format("Processed rule[%s]: %s", rule.getId(), rule.getName() ));
  }
}
