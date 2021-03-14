package com.baloise.open.maven.sonar.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder(toBuilder = true)
public class Issue {

  private String engineID;
  private String ruleId;
  private Location primarylocation;
  private Set<Location> secondaryLocations;
  private int effortMinutes;

}
