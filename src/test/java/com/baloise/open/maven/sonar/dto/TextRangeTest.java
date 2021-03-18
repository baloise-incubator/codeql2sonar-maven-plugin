package com.baloise.open.maven.sonar.dto;

import com.baloise.open.maven.PropertyReflectionTest;
import org.junit.jupiter.api.Test;

class TextRangeTest extends PropertyReflectionTest {

  @Test
  void verifyProperties() {
    assertNumberOfProperties(TextRange.class, 4);
  }

}
