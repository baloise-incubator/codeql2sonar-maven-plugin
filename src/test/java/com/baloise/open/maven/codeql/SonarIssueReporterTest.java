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
    assertThrows(MojoExecutionException.class, () -> new SonarIssueReporter(null, null, false, null).execute());
    assertThrows(MojoExecutionException.class, () -> new SonarIssueReporter("   ", null, false, null).execute());
    assertThrows(MojoExecutionException.class, () -> new SonarIssueReporter(null, "   ", false, null).execute());
    assertThrows(MojoExecutionException.class, () -> new SonarIssueReporter("   ", "   ", false, null).execute());
  }

  @Test
  void testInvalidSarifFile() {
    final File input = new File("src/test/resources/anyOther.json");
    assertTrue(input.isFile());

    final SonarIssueReporter testee = new SonarIssueReporter(input.getAbsolutePath());
    final MojoExecutionException mojoExecutionException = assertThrows(MojoExecutionException.class, testee::execute);
    assertEquals("$schema not found in root object.", mojoExecutionException.getMessage());
  }


  @Test
  void testHappyCase() throws MojoExecutionException, IOException {
    testByResourceFiles("src/test/resources/example.sarif", "src/test/resources/expectedResult.json", false);
  }

  @Test
  void testWithFilter() throws MojoExecutionException, IOException {
    testByResourceFiles("src/test/resources/InputWithTestResource.sarif", "src/test/resources/expectedResult.json", true);
  }

  @Test
  void multiModuleTest() throws MojoExecutionException, IOException {
    testByResourceFiles("src/test/resources/multiModuleInput.sarif", "src/test/resources/multiModuleResult.json", false);
  }

  private void testByResourceFiles(String inputSarifFIle, String expectedResultFile, boolean ignoreTests) throws MojoExecutionException, IOException {
    final File input = new File(inputSarifFIle);
    assertTrue(input.isFile());
    final File expected = new File(expectedResultFile);
    assertTrue(expected.isFile());

    final SonarIssueReporter testee = new SonarIssueReporter(input.getAbsolutePath(), null, ignoreTests, null);
    final StringWriter testwriter = new StringWriter();
    testee.setWriter(testwriter);
    testee.execute();

    final String expectedString = FileUtils.fileRead(expected).trim().replace("\n", "").replace("\r", "");
    assertEquals(expectedString, testwriter.toString().trim().replace("\n", "").replace("\r", ""));
  }
}
