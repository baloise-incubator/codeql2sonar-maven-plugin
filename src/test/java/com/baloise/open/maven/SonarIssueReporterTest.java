package com.baloise.open.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    assertTrue(mojoExecutionException.getMessage().startsWith("$schema not found in root object."));
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

  @Nested
  class SrcDirectoryFromPom {

    @Test
    void emptyPluginContextDoesNotCrash() {
      assertNotNull(new SonarIssueReporter().getSourceDirectoryFromPom(null));
    }

    @Test
    void emptyCompiledSourcesResultsDefault() {
      final MavenProject mockedMvnPrj = Mockito.mock(MavenProject.class);
      Mockito.when(mockedMvnPrj.getCompileSourceRoots()).thenReturn(Collections.emptyList());
      Mockito.when(mockedMvnPrj.getTestCompileSourceRoots()).thenReturn(Collections.emptyList());

      Map<String, MavenProject> map = new HashMap<>();
      map.put("project", mockedMvnPrj);

      final List<String> result = new SonarIssueReporter().getSourceDirectoryFromPom(map);

      assertAll(
          () -> assertNotNull(result),
          () -> assertFalse(result.isEmpty()),
          () -> assertEquals(1, result.size()),
          () -> assertTrue(result.contains("src/"))
      );
    }

    @Test
    void compiledSourcesConsidered() {
      final MavenProject mockedMvnPrj = Mockito.mock(MavenProject.class);
      Mockito.when(mockedMvnPrj.getCompileSourceRoots()).thenReturn(Collections.singletonList("/usr/test/project/mockedSrc/".replace("/", File.separator)));
      Mockito.when(mockedMvnPrj.getTestCompileSourceRoots()).thenReturn(Collections.singletonList("/usr/test/project/mockedSrcTest/".replace("/", File.separator)));

      final File mockedFile = Mockito.mock(File.class);
      Mockito.when(mockedFile.getAbsolutePath()).thenReturn("/usr/test/project".replace("/", File.separator));
      Mockito.when(mockedMvnPrj.getBasedir()).thenReturn(mockedFile);

      Map<String, MavenProject> map = new HashMap<>();
      map.put("project", mockedMvnPrj);

      final List<String> result = new SonarIssueReporter().getSourceDirectoryFromPom(map);

      assertAll(
          () -> assertNotNull(result),
          () -> assertFalse(result.isEmpty()),
          () -> assertEquals(2, result.size()),
          () -> assertTrue(result.contains("mockedSrc/")),
          () -> assertTrue(result.contains("mockedSrcTest/"))
      );
    }

  }

}
