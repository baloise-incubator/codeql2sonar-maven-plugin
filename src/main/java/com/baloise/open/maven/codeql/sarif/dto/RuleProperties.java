package com.baloise.open.maven.codeql.sarif.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Arrays;

@Data
@Builder(toBuilder = true)
public class RuleProperties {

  private String id;
  private String name;
  private String description;
  private String[] tags;
  private String kind;
  private String precision;
  private Severity severity;

  public enum Severity {
    warning,
    error,
    recommendation
  }

  @Override
  public String toString() {
    return "RuleProperties{" +
            "id='" + id + '\'' +
            ", tags=" + Arrays.toString(tags) +
            ", kind='" + kind + '\'' +
            ", precision='" + precision + '\'' +
            ", severity=" + severity +
            '}';
  }
}
