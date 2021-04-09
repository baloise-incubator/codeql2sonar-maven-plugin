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
    codeQlResults.add(result);
    mappedIssues.getIssues().add(mapResult(result));
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

  // TODO: verify if mapping is as expected
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

      case error:
        // consider precision as first criteria
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
        if (level == Rule.Level.error) {
          return Issue.Severity.BLOCKER;
        }
        return Issue.Severity.CRITICAL;
    }

    return null;
  }

  private Issue.Severity mapRuleLevelToSeverity(Rule.Level level) {
    switch (level) {
      case none:
      case note:
        return Issue.Severity.MINOR;
      case warning:
        return Issue.Severity.MAJOR;
      case error:
        return Issue.Severity.CRITICAL;
    }
    return null;
  }

  // TODO: verify if mapping is as expected
  Issue.Type mapType(Issue.Severity severity) {
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
    // TODO: verify if offset needs to be recalculated
    return TextRange.builder()
            .startLine(region.getStartLine())
            .endLine(region.getStartLine())
            .startColumn(region.getStartColumn())
            .endColumn(region.getEndColumn())
            .build();
  }

  public String getSummary() {
    return String.format("parsed %d Rules, %d Results from codeQL resulting in %d issues.",
            codeQlRules.size(), codeQlResults.size(), mappedIssues.getIssues().size());
  }

  public List<Issue> getMappedIssues(String[] patternsToExclude) {
    return (patternsToExclude != null && patternsToExclude.length > 0)
            ? mappedIssues.getIssues().stream().filter(issue -> !isMatchingExlusionPattern(issue, patternsToExclude)).collect(Collectors.toList())
            : mappedIssues.getIssues();
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
