package com.baloise.open.maven.sonar.dto;

import com.baloise.open.maven.PropertyReflectionTest;
import org.junit.jupiter.api.Test;

class IssuesTest extends PropertyReflectionTest {

  @Test
  void verifyProperties() {
    assertNumberOfProperties(Issues.class, 1);
  }


}
