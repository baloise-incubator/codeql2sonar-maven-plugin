/*
 Copyright 2021 Baloise Group

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.baloise.open.maven;

import com.baloise.open.maven.codeql.sarif.ConsoleParser;
import com.baloise.open.maven.codeql.sarif.SarifParser;
import com.baloise.open.maven.sonar.SonarIssueMapper;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.inject.Inject;
import java.io.*;

@Mojo(name = "SonarIssueReporter", defaultPhase = LifecyclePhase.VERIFY)
public class SonarIssueReporter extends AbstractMojo {

  private static final String ERR_FILE_SUFFIX = "Verify parameter codeql2sonar.sarif.inputfile in your pom.xml";
  private static final String DEFAULT_OURPUT_FILE = "./target/sonar/codeql2sonar.json";
  private static final String DEFAULT_IGNORE_TEST_FLAG = "false";

  @Parameter(property = "codeql2sonar.sarif.inputfile")
  private String sarifInputFile;

  @Parameter(property = "codeql2sonar.sarif.outputfile", defaultValue = DEFAULT_OURPUT_FILE)
  private String target;

  @Parameter(property = "codeql2sonar.sarif.ignoreTests", defaultValue = DEFAULT_IGNORE_TEST_FLAG)
  private boolean ignoreTests;

  @Parameter(property = "codeql2sonar.sarif.path.excludes")
  private String[] pathExlcudes;

  @Setter
  private Writer writer;

  public SonarIssueReporter(String sarifInputFile) {
    this.sarifInputFile = sarifInputFile;
    /* set defaults */
    this.target = DEFAULT_OURPUT_FILE.replace("/", File.separator);
    this.ignoreTests = false;
  }

  public SonarIssueReporter(String sarifInputFile, String target, boolean ignoreTests, String[] pathExlcudes) {
    this.sarifInputFile = sarifInputFile;
    this.target = target;
    this.ignoreTests = ignoreTests;
    this.pathExlcudes = pathExlcudes;
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
      try (final Writer resultWriter = getWriter()) {
        writeResult(sonarIssueMapper, resultWriter);
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
            .toJson(sonarIssueMapper.getMappedIssues(getPatternsToExclude()), writer);
    writer.flush();
  }

  private String[] getPatternsToExclude() {
    if (ignoreTests) {
      final String[] testExclusion = {"src/test/"};
      return pathExlcudes == null ? testExclusion : ArrayUtils.addAll(pathExlcudes, testExclusion);
    }
    return pathExlcudes;
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
