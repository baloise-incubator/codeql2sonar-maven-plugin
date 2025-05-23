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
package com.baloise.open.maven.sonar;

import com.baloise.open.maven.codeql.sarif.ParserCallback;
import com.baloise.open.maven.codeql.sarif.dto.*;
import com.baloise.open.maven.sonar.dto.Issue;
import com.baloise.open.maven.sonar.dto.Issues;
import com.baloise.open.maven.sonar.dto.Location;
import com.baloise.open.maven.sonar.dto.TextRange;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SonarIssueMapper implements ParserCallback {

  private final ArrayList<Result> codeQlResults = new ArrayList<>();
  private final ArrayList<Rule> codeQlRules = new ArrayList<>();
  private final Issues mappedIssues = new Issues();
  private Driver driver;
  @Getter
  private String version;
  @Getter
  private String schema;

  @Override
  public void onFinding(Result result) {
    if (result != null) {
      codeQlResults.add(result);
      mappedIssues.add(mapResult(result));
    }
  }

  @Override
  public void onVersion(String version) {
    this.version = version;
  }

  @Override
  public void onSchema(String schema) {
    this.schema = schema;
  }

  @Override
  public void onDriver(Driver driver) {
    this.driver = driver;
  }

  @Override
  public void onRule(Rule rule) {
    codeQlRules.add(rule);
  }

  private Issue mapResult(Result result) {
    final Issue.Severity severity = mapSeverity(result.getRuleId());
    return Issue.builder()
            .ruleId(result.getRuleId())
            .primaryLocation(mapPrimaryLocation(result))
            .secondaryLocations(mapSecondaryLocations(result))
            .severity(severity)
            .type(mapType(severity))
            .engineId(driver != null ? driver.toString() : SonarIssueMapper.class.getSimpleName())
            .build();
  }

  Issue.Severity mapSeverity(String ruleId) {
    final Rule matchingRule = codeQlRules.stream().filter(rule -> rule.getId().equals(ruleId)).findFirst().orElse(null);
    if (matchingRule != null && matchingRule.getProperties().getSeverity() != null) {
      return mapRuleToIssueSeverity(matchingRule.getLevel(), matchingRule.getProperties());
    }
    return Issue.Severity.INFO;
  }

  Issue.Severity mapRuleToIssueSeverity(final Rule.Level level, final RuleProperties properties) {
    if (properties == null || properties.getSeverity() == null) {
      // without properties the only basis to map severity is the rule level.
      return (level == null) ? null : mapRuleLevelToSeverity(level);
    }

    final RuleProperties.Severity ruleSeverity = properties.getSeverity();
    final String rulePrecision = properties.getPrecision();

    switch (ruleSeverity) {
      case recommendation:
        return Issue.Severity.INFO;
      case warning:
        // consider precision as first criteria
        return mapRuleSeverityWarning(level, rulePrecision);
      case error:
        // consider precision as first criteria
        return mapRuleSeverityError(level, rulePrecision);
    }

    return null;
  }

  private Issue.Severity mapRuleSeverityError(Rule.Level level, String rulePrecision) {
    if (rulePrecision != null) {
      switch (rulePrecision.toLowerCase()) {
        case "medium":
        case "high":
          return Issue.Severity.CRITICAL;
        case "very-high":
          return Issue.Severity.BLOCKER;
        default:
          // not decisive yet
      }
    }
    // if not set or unknown consider level as second criteria
    if (level == Rule.Level.ERROR) {
      return Issue.Severity.BLOCKER;
    }
    return Issue.Severity.CRITICAL;
  }

  private Issue.Severity mapRuleSeverityWarning(Rule.Level level, String rulePrecision) {
    if (rulePrecision != null) {
      switch (rulePrecision.toLowerCase()) {
        case "medium":
          return Issue.Severity.MINOR;
        case "high":
          return Issue.Severity.MAJOR;
        case "very-high":
          return Issue.Severity.CRITICAL;
        default:
          // not decisive yet
      }
    }
    // if not set or unknown consider level as second criteria
    return (level == null) ? Issue.Severity.MINOR : mapRuleLevelToSeverity(level);
  }

  private Issue.Severity mapRuleLevelToSeverity(Rule.Level level) {
    switch (level) {
      case NONE:
      case NOTE:
        return Issue.Severity.MINOR;
      case WARNING:
        return Issue.Severity.MAJOR;
      case ERROR:
        return Issue.Severity.CRITICAL;
      default:
        return null;
    }
  }

  Issue.Type mapType(Issue.Severity severity) {
    if (severity == null) {
      return null;
    }
    switch (severity) {
      case INFO:
      case MINOR:
      case MAJOR:
        return Issue.Type.CODE_SMELL;
      case BLOCKER:
        return Issue.Type.BUG;
      case CRITICAL:
      default:
        return Issue.Type.VULNERABILITY;
    }
  }

  Set<Location> mapSecondaryLocations(Result result) {
    final List<com.baloise.open.maven.codeql.sarif.dto.Location> locations = result.getLocations();
    if (locations == null || locations.size() < 2) {
      return null;
    }
    return locations.stream().skip(1).map(location -> mapLocation(location, result.getMessage())).collect(Collectors.toSet());
  }

  Location mapPrimaryLocation(Result result) {
    final List<com.baloise.open.maven.codeql.sarif.dto.Location> locations = result.getLocations();
    if (locations == null || locations.isEmpty()) {
      return null;
    }
    return mapLocation(locations.get(0), result.getMessage());
  }

  private Location mapLocation(com.baloise.open.maven.codeql.sarif.dto.Location location, String message) {
    return Location.builder()
            .filePath(location.getUri())
            .message(message)
            .textRange(mapTextRange(location.getRegion()))
            .build();
  }

  private TextRange mapTextRange(Region region) {
    if (region == null) {
      return null;
    }
    return TextRange.builder()
            .startLine(region.getStartLine())
            .endLine(region.getStartLine())
            .startColumn(region.getStartColumn())
            .endColumn(region.getEndColumn())
            .build();
  }

  public String getSummary() {
    return "parsed %d Rules, %d Results from codeQL resulting in %d issues.".formatted(
            codeQlRules.size(), codeQlResults.size(), mappedIssues.get().size());
  }

  public Issues getMappedIssues(String[] patternsToExclude) {
    return (patternsToExclude != null && patternsToExclude.length > 0)
            ? mappedIssues.applyFilter((issue -> !isMatchingExlusionPattern(issue, patternsToExclude)))
            : mappedIssues;
  }

  private boolean isMatchingExlusionPattern(Issue issue, String[] patternsToExclude) {
    final Location primaryLocation = issue.getPrimaryLocation();
    if (primaryLocation == null) {
      return false;
    }
    final String filePath = primaryLocation.getFilePath();
    return Arrays.stream(patternsToExclude)
            .anyMatch(pattern -> Pattern.compile(".*" + pattern + ".*", Pattern.CASE_INSENSITIVE).matcher(filePath).matches());
  }

}
