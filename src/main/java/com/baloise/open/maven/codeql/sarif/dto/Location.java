package com.baloise.open.maven.codeql.sarif.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Location {

  private String uri;
  private String uriBaseId;
  private int index;
  private Region region;

  @Override
  public String toString() {
    return String.format("%s,%s"
            , uri
            , region != null ? region.toString() : "n/a");
  }
}
