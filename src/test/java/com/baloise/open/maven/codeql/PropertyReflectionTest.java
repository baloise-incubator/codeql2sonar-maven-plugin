package com.baloise.open.maven.codeql;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropertyReflectionTest {

  protected void assertNumberOfProperties(Class<?> className, long expectedCount) {
    final Field[] declaredFields = className.getDeclaredFields();
    assertEquals(expectedCount, Arrays.stream(declaredFields).filter(field -> !field.getName().contains("$jacocoData")).count());
  }

}
