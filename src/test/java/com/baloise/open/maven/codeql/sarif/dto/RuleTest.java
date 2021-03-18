package com.baloise.open.maven.codeql.sarif.dto;

import com.baloise.open.maven.PropertyReflectionTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleTest extends PropertyReflectionTest {

  @Test
  void verifyProperties() {
    assertNumberOfProperties(Rule.class, 6);
  }

  @Test
  void verifyStringOutput() {
    final Rule testee = Rule.builder().build();
    assertEquals("Rule[null]: 'null'; null", testee.toString());
    testee.setId("MyTestId");
    assertEquals("Rule[MyTestId]: 'null'; null", testee.toString());
    testee.setLevel(Rule.Level.warning);
    assertEquals("Rule[MyTestId]-warning: 'null'; null", testee.toString());
    testee.setName("DaRulezName");
    assertEquals("Rule[MyTestId]-warning: 'DaRulezName'; null", testee.toString());
    testee.setProperties(RuleProperties.builder().build());
    assertEquals("Rule[MyTestId]-warning: 'DaRulezName'; RuleProperties{id='null', tags=null, kind='null', precision='null', severity=null}", testee.toString());
  }

  @Test
  void verifyEnum() {
    final Rule.Level[] levels = Rule.Level.values();
    assertEquals(4, levels.length);
    assertEquals(Rule.Level.warning, Rule.Level.valueOf("warning"));
    assertEquals(Rule.Level.error, Rule.Level.valueOf("error"));
    assertEquals(Rule.Level.note, Rule.Level.valueOf("note"));
    assertEquals(Rule.Level.none, Rule.Level.valueOf("none"));
  }

}
