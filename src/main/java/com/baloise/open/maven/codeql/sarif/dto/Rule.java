package com.baloise.open.maven.codeql.sarif.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Rule {
  private String id;
  private String name;
  private String shortDescription;
  private String fullDescription;
  private Level level;
  private RuleProperties properties;

  @Override
  public String toString() {
    return String.format("Rule[%s]%s: '%s'; %s"
            ,id
            ,level!=null ? "-" + level : ""
            ,name
            ,properties);
  }

  public enum Level {
    warning, // The rule specified by ruleId was evaluated and a problem was found.
    error,   // The rule specified by ruleId was evaluated and a serious problem was found.
    note,    // The rule specified by ruleId was evaluated and a minor problem or an opportunity to improve the code was found.
    none     // The concept of “severity” does not apply to this result because the kind property (§3.27.9) has a value other than "fail".
  }
}
