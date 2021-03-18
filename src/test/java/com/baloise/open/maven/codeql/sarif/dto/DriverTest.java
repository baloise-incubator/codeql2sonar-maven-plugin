package com.baloise.open.maven.codeql.sarif.dto;

import com.baloise.open.maven.PropertyReflectionTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DriverTest extends PropertyReflectionTest {

  @Test
  void verifyProperties() {
    assertNumberOfProperties(Driver.class, 3);
  }

  @Test
  void verifyStringOutput() {
    final Driver testee = Driver.builder().build();
    assertEquals("n/a n/a", testee.toString());
    testee.setOrganization("DriverOrg");
    assertEquals("DriverOrg n/a", testee.toString());
    testee.setName("DriverName");
    assertEquals("DriverOrg DriverName", testee.toString());
    testee.setSemanticVersion("1.2.3");
    assertEquals("DriverOrg DriverName v1.2.3", testee.toString());
  }
}
