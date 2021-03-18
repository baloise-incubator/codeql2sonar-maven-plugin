package com.baloise.open.maven.codeql.sarif.dto;

import com.baloise.open.maven.PropertyReflectionTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegionTest extends PropertyReflectionTest {

  @Test
  void verifyProperties() {
    assertNumberOfProperties(Region.class, 3);
  }

  @Test
  void verifyStringOutput() {
    final Region testee = Region.builder().build();
    assertEquals("Line 0, Column 0", testee.toString());
    testee.setStartLine(75);
    assertEquals("Line 75, Column 0", testee.toString());
    testee.setStartColumn(9);
    assertEquals("Line 75, Column 9", testee.toString());
    testee.setEndColumn(14);
    assertEquals("Line 75, Column 9:14", testee.toString());
  }

}
