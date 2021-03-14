package com.baloise.open.maven.sonar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Location {

  private String message;
  private String filePath;
  private TextRange textRange;

}
