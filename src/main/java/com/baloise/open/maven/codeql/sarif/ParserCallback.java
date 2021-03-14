package com.baloise.open.maven.codeql.sarif;

import com.baloise.open.maven.codeql.sarif.dto.Result;

public interface ParserCallback {

  void onFinding(Result result);

  void onVersion(String version);
}
