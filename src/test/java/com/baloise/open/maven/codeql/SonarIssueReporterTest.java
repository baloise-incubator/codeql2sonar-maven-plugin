package com.baloise.open.maven.codeql;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SonarIssueReporterTest {

  @Test
  void execute_FileMissing_ExceptionExpected() {
    assertThrows(MojoExecutionException.class, () ->  new SonarIssueReporter(null).execute());
    assertThrows(MojoExecutionException.class, () ->  new SonarIssueReporter("   ").execute());
  }
}
