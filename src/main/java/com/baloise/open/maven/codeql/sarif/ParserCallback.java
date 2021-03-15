package com.baloise.open.maven.codeql.sarif;

import com.baloise.open.maven.codeql.sarif.dto.Result;
import com.baloise.open.maven.codeql.sarif.dto.Rule;

public interface ParserCallback {

  void onFinding(Result result);

  void onVersion(String version);

  void onRule(Rule rule);
}
