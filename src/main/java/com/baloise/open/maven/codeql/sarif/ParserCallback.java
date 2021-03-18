package com.baloise.open.maven.codeql.sarif;

import com.baloise.open.maven.codeql.sarif.dto.Driver;
import com.baloise.open.maven.codeql.sarif.dto.Result;
import com.baloise.open.maven.codeql.sarif.dto.Rule;

public interface ParserCallback {

  void onFinding(Result result);

  void onVersion(String version);

  void onSchema(String schema);

  void onDriver(Driver driver);

  void onRule(Rule rule);
}
