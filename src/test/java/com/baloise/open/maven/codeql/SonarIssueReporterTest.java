package com.baloise.open.maven.codeql;

import com.baloise.open.maven.SonarIssueReporter;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class SonarIssueReporterTest {

  @Test
  void execute_FileMissing_ExceptionExpected() {
    assertThrows(MojoExecutionException.class, () ->  new SonarIssueReporter(null, null).execute());
    assertThrows(MojoExecutionException.class, () ->  new SonarIssueReporter("   ", null).execute());
    assertThrows(MojoExecutionException.class, () ->  new SonarIssueReporter(null, "   ").execute());
    assertThrows(MojoExecutionException.class, () ->  new SonarIssueReporter("   ", "   ").execute());
  }

  @Test
  void testInvalidSarifFile() {
    final File input = new File("src/test/resources/anyOther.json");
    assertTrue(input.isFile());

    final SonarIssueReporter testee = new SonarIssueReporter(input.getAbsolutePath(), null);
    final MojoExecutionException mojoExecutionException = assertThrows(MojoExecutionException.class, testee::execute);
    assertEquals("$schema not found in root object.", mojoExecutionException.getMessage());
  }

  @Test
  void testHappyCase() throws MojoExecutionException, IOException {
    final File input = new File("src/test/resources/example.sarif");
    assertTrue(input.isFile());
    final File expected = new File("src/test/resources/expectedResult.json");
    assertTrue(expected.isFile());

    final SonarIssueReporter testee = new SonarIssueReporter(input.getAbsolutePath(), null);
    final StringWriter testwriter = new StringWriter();
    testee.setWriter(testwriter);
    testee.execute();

    final String expectedString = FileUtils.fileRead(expected).trim().replace("\n", "").replace("\r", "");
    assertEquals(expectedString, testwriter.toString().trim().replace("\n", "").replace("\r", ""));
  }
}
