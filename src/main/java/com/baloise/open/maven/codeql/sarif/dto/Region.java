package com.baloise.open.maven.codeql.sarif.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Region {

  private int startLine;
  private int startColumn;
  private int endColumn;

  @Override
  public String toString() {
    return String.format("Line %d, Column %d%s"
            ,startLine
            ,startColumn
            , endColumn > 0 ? ":"+endColumn : "");
  }
}
