package com.baloise.open.maven.sonar;

import com.baloise.open.maven.codeql.sarif.dto.*;
import com.baloise.open.maven.sonar.dto.Issue;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class SonarIssueMapperTest {

  public static final String TEST_RULE_ID = "testRuleId";

  @Test
  void testParserCallback_onFinding() {
    final SonarIssueMapper testee = new SonarIssueMapper();

    assertEquals("parsed 0 Rules, 0 Results from codeQL resulting in 0 issues.", testee.getSummary());
    assertEquals(0, testee.getMappedIssues().size());

    testee.onFinding(createTestResult());

    assertEquals("parsed 0 Rules, 1 Results from codeQL resulting in 1 issues.", testee.getSummary());
    assertEquals(1, testee.getMappedIssues().size());
  }

  @Test
  void testMapperInjected() {
    final SonarIssueMapper testee = new SonarIssueMapper();

    testee.onDriver(Driver.builder().name("driverName").organization("DriverOrg").semanticVersion("DriverVersion").build());
    testee.onRule(createTestRule());
    testee.onFinding(createTestResult());

    assertEquals("parsed 1 Rules, 1 Results from codeQL resulting in 1 issues.", testee.getSummary());
    assertEquals(1, testee.getMappedIssues().size());

    final Issue issue = testee.getMappedIssues().get(0);
    assertEquals(TEST_RULE_ID, issue.getRuleId());
    assertEquals(Issue.Severity.BLOCKER, issue.getSeverity());
    assertNotNull(issue.getPrimaryLocation());
    assertNull(issue.getSecondaryLocations());
    assertEquals("DriverOrg driverName vDriverVersion", issue.getEngineId());
  }

  @Test
  void testParserCallback_onVersion() {
    final SonarIssueMapper testee = new SonarIssueMapper();
    assertNull(testee.getVersion());
    testee.onVersion("AnyVersion");
    assertEquals("AnyVersion", testee.getVersion());
  }

  @Test
  void testParserCallback_onSchema() {
    final SonarIssueMapper testee = new SonarIssueMapper();
    assertNull(testee.getSchema());
    testee.onSchema("AnySchema");
    assertEquals("AnySchema", testee.getSchema());
  }

  @Test
  void testParserCallback_onRule() {
    final SonarIssueMapper testee = new SonarIssueMapper();
    assertEquals("parsed 0 Rules, 0 Results from codeQL resulting in 0 issues.", testee.getSummary());
    testee.onRule(createTestRule());
    assertEquals("parsed 1 Rules, 0 Results from codeQL resulting in 0 issues.", testee.getSummary());
  }

  @Test
  void testMapSeverity() {
  }

  @Test
  void testMapRuleToIssueSeverity_LenientInput() {
    final SonarIssueMapper testee = new SonarIssueMapper();
    assertNull(testee.mapRuleToIssueSeverity(null, null));
    assertNull(testee.mapRuleToIssueSeverity(null, RuleProperties.builder().build()));
    assertEquals(Issue.Severity.MINOR, testee.mapRuleToIssueSeverity(Rule.Level.none, RuleProperties.builder().build()));
    assertEquals(Issue.Severity.MINOR, testee.mapRuleToIssueSeverity(Rule.Level.note, RuleProperties.builder().build()));
    assertEquals(Issue.Severity.MAJOR, testee.mapRuleToIssueSeverity(Rule.Level.warning, RuleProperties.builder().build()));
    assertEquals(Issue.Severity.CRITICAL, testee.mapRuleToIssueSeverity(Rule.Level.error, RuleProperties.builder().build()));
  }

  @Test
  void testMapRuleToIssueSeverity() {

  }

  @Test
  void testMapType() {
    final SonarIssueMapper testee = new SonarIssueMapper();
    assertEquals(Issue.Type.CODE_SMELL, testee.mapType(Issue.Severity.INFO));
    assertEquals(Issue.Type.CODE_SMELL, testee.mapType(Issue.Severity.MINOR));
    assertEquals(Issue.Type.CODE_SMELL, testee.mapType(Issue.Severity.MAJOR));
    assertEquals(Issue.Type.BUG, testee.mapType(Issue.Severity.BLOCKER));
    assertEquals(Issue.Type.VULNERABILITY, testee.mapType(Issue.Severity.CRITICAL));
  }

  @Test
  void testMapSecondaryLocations() {
  }

  @Test
  void testMapPrimaryLocation() {
  }

  private Rule createTestRule() {
    return Rule.builder()
            .id(TEST_RULE_ID)
            .level(Rule.Level.error)
            .name("TestRuleName")
            .properties(RuleProperties.builder()
                    .severity(RuleProperties.Severity.error)
                    .id(TEST_RULE_ID)
                    .build())
            .build();
  }

  private Result createTestResult() {
    return Result.builder()
            .message("TestMessage")
            .ruleId(TEST_RULE_ID)
            .ruleIndex(4)

            .locations(Collections.singletonList(createTestLocation()))
            .build();
  }

  private Location createTestLocation() {
    return Location.builder()
            .uri("testUri")
            .uriBaseId("testUriBaseId")
            .index(4)
            .region(Region.builder().startColumn(1).endColumn(2).startLine(3).startLine(4).build())
            .build();
  }
}
