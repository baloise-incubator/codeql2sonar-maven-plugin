package com.baloise.open.maven.sonar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class TextRange {

  private int startLine;
  private int endLine;
  private int startColumn;
  private int endColumn;

}
