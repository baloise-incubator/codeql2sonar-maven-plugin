/*
 Copyright 2021 Baloise Group

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.baloise.open.maven.sonar.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Main object of exported Sonar Issue Report.
 * See also Generic Issue Import Format https://docs.sonarqube.org/latest/analysis/generic-issue/
 */
public final class Issues {

  private final List<Issue> issues = new ArrayList<>();

  public List<Issue> getIssues() {
    return issues;
  }

  public Issues applyFilter(Predicate<Issue> predicate) {
    final Issues filteredIssues = new Issues();
    filteredIssues.getIssues().addAll(this.getIssues().stream().filter(predicate).collect(Collectors.toList()));
    return filteredIssues;
  }
}
