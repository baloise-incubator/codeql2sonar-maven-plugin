package com.baloise.open.maven.sonar;

import com.baloise.open.maven.codeql.sarif.ParserCallback;
import com.baloise.open.maven.codeql.sarif.dto.Driver;
import com.baloise.open.maven.codeql.sarif.dto.Region;
import com.baloise.open.maven.codeql.sarif.dto.Result;
import com.baloise.open.maven.codeql.sarif.dto.Rule;
import com.baloise.open.maven.sonar.dto.Issue;
import com.baloise.open.maven.sonar.dto.Location;
import com.baloise.open.maven.sonar.dto.TextRange;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SonarIssueMapper implements ParserCallback {

  private final ArrayList<Result> codeQlResults = new ArrayList<>();
  private final ArrayList<Rule> codeQlRules = new ArrayList<>();
  @Getter
  private final List<Issue> mappedIssues = new ArrayList<>();
  private Driver driver;
  @Getter
  private String version;
  @Getter
  private String schema;

  @Override
  public void onFinding(Result result) {
    codeQlResults.add(result);
    mappedIssues.add(mapResult(result));
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
    return Issue.builder()
            .ruleId(result.getRuleId())
            .primarylocation(mapPrimaryLocation(result))
            .secondaryLocations(mapSecondaryLocations(result))
            .engineID(driver != null ? driver.toString() : SonarIssueMapper.class.getSimpleName())
            .build();
  }

  private Set<Location> mapSecondaryLocations(Result result) {
    final List<com.baloise.open.maven.codeql.sarif.dto.Location> locations = result.getLocations();
    if (locations == null || locations.size() < 2) {
      return null;
    }
    return locations.stream().skip(1).map(location -> mapLocation(location, result.getMessage())).collect(Collectors.toSet());
  }

  private Location mapPrimaryLocation(Result result) {
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
    // TODO: Obviously the offset needs to be recalculated
    return TextRange.builder()
            .startLine(region.getStartLine())
            .endLine(region.getStartLine())
            .startColumn(region.getStartColumn())
            .endColumn(region.getEndColumn())
            .build();
  }
}
