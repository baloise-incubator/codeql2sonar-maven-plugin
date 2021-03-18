package com.baloise.open.maven.sonar.dto;

import com.baloise.open.maven.PropertyReflectionTest;
import org.junit.jupiter.api.Test;

class IssueTest extends PropertyReflectionTest {

  @Test
  void verifyProperties() {
    assertNumberOfProperties(Issue.class, 5);
  }

}
