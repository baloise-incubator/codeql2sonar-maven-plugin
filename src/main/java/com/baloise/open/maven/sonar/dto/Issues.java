package com.baloise.open.maven.sonar.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Main object of exported Sonar Issue Report.
 * See also Generic Issue Import Format https://docs.sonarqube.org/latest/analysis/generic-issue/
 */
@Getter
public class Issues {

  private final List<Issue> issues = new ArrayList<>();

}
