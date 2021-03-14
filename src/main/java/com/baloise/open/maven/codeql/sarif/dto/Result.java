package com.baloise.open.maven.codeql.sarif.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class Result {

  private String ruleId;
  private int ruleIndex;
  private String message;
  private List<Location> locations;

  @Override
  public String toString() {
    String result = String.format("Process issue of rule '%s': '%s'", ruleId, message);
    if (locations != null && !locations.isEmpty()) {
      result += "\n" + locations.get(0).toString();
    }
    return result;
  }
}
