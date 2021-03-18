package com.baloise.open.maven.codeql.sarif.dto;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder(toBuilder = true)
public class Driver {

  private String name;
  private String organization;
  private String semanticVersion;

  @Override
  public String toString() {
    return String.format("%s %s %s",
            organization==null ? "n/a" : organization,
            name == null ? "n/a" : name,
            StringUtils.isBlank(semanticVersion) ? "" : "v" + semanticVersion).trim();
  }
}
