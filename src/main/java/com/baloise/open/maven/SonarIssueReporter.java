package com.baloise.open.maven;

import com.baloise.open.maven.codeql.sarif.ConsoleParser;
import com.baloise.open.maven.codeql.sarif.SarifParser;
import com.baloise.open.maven.sonar.SonarIssueMapper;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.inject.Inject;
import java.io.*;

// TODO: add filter to exclude TESTS
// TODO: add options to configure level to log output

@Mojo(name = "SonarIssueReporter", defaultPhase = LifecyclePhase.VERIFY)
public class SonarIssueReporter extends AbstractMojo {

  private static final String ERR_FILE_SUFFIX = "Verify parameter codeql2sonar.sarif.inputfile in your pom.xml";

  @Parameter(property = "codeql2sonar.sarif.inputfile")
  private String sarifInputFile;

  @Parameter(property = "codeql2sonar.sarif.outputfile", defaultValue = "target/sonar/codeql2sonar.json")
  private String target;

  @Setter
  private Writer writer;

  public SonarIssueReporter(String sarifInputFile, String target) {
    this.sarifInputFile = sarifInputFile;
    this.target = target;
  }

  @Inject
  public SonarIssueReporter() {
  }

  public void execute() throws MojoExecutionException {
    getLog().info("execute SonarIssueReporter");
    try {
      final SonarIssueMapper sonarIssueMapper = new SonarIssueMapper();
      final File inputFile = readSarifFile(this.sarifInputFile);
      SarifParser.execute(inputFile, new ConsoleParser(getLog()), sonarIssueMapper);
      try (final Writer writer = getWriter()) {
        writeResult(sonarIssueMapper, writer);
      }
    } catch (Exception e) {
      throw new MojoExecutionException(e.getMessage());
    }
  }

  private Writer getWriter() throws IOException {
    return (writer == null) ? new FileWriter(target) : writer;
  }

  private void writeResult(SonarIssueMapper sonarIssueMapper, Writer writer) throws IOException {
    getLog().info("writing target " + target);
    new GsonBuilder().setPrettyPrinting().create()
            .toJson(sonarIssueMapper.getMappedIssues(), writer);
    writer.flush();
  }

  private File readSarifFile(String sarifInputFile) throws MojoExecutionException {
    if (StringUtils.isBlank(sarifInputFile)) {
      throw new MojoExecutionException("No Sarif file provided. " + ERR_FILE_SUFFIX);
    }

    getLog().info("read " + sarifInputFile);
    final File result = new File(sarifInputFile);

    if (!result.isFile()) {
      throw new MojoExecutionException(String.format("Specified path is not a valid file: '%s'. %s", sarifInputFile, ERR_FILE_SUFFIX));
    } else if (!result.canRead()) {
      throw new MojoExecutionException(String.format("Specified file is not readable: '%s'. %s", sarifInputFile, ERR_FILE_SUFFIX));
    }

    return validate(result);
  }

  private File validate(File sarifFile) throws MojoExecutionException {
    try {
      final JsonObject rootObject = JsonParser.parseReader(new FileReader(sarifInputFile)).getAsJsonObject();
      if (!rootObject.has("$schema")) {
        throw new MojoExecutionException(sarifFile
                , "$schema not found in root object."
                , String.format("$schema not found in root object - provided file %s does not seem to be a valid sarif file", sarifFile.getName()));
      }
    } catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
    return sarifFile;
  }
}
