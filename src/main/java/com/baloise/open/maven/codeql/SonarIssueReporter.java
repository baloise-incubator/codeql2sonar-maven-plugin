package com.baloise.open.maven.codeql;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.inject.Inject;
import java.io.File;

@Mojo(name = "SonarIssueReporter", defaultPhase = LifecyclePhase.VERIFY)
public class SonarIssueReporter extends AbstractMojo {

  private static final String ERR_FILE_SUFFIX = "Verify parameter codeql2sonar.sarif.inputfile in your pom.xml";

  @Parameter(property = "codeql2sonar.sarif.inputfile")
  private String sarifInputFile;

  @Parameter(property = "codeql2sonar.sarif.outputfile", defaultValue = "target/sonar/codeql2sonar.json")
  private String target;

  public SonarIssueReporter(String sarifInputFile) {
    this.sarifInputFile = sarifInputFile;
  }

  @Inject
  public SonarIssueReporter() {  }

  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("SonarIssueReporter works!");

    final File sarifFile = readSarifFile(sarifInputFile);

    // TODO: validate Sarif file

    // TODO: parse result

    // TODO: write result

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

    return result;
  }

}
