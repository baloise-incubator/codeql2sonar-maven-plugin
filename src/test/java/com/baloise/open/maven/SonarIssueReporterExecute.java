package com.baloise.open.maven;

import com.baloise.open.maven.codeql.sarif.ParserCallback;
import com.baloise.open.maven.codeql.sarif.SarifParser;
import com.baloise.open.maven.codeql.sarif.dto.Driver;
import com.baloise.open.maven.codeql.sarif.dto.Result;
import com.baloise.open.maven.codeql.sarif.dto.Rule;
import com.baloise.open.maven.sonar.SonarIssueMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class SonarIssueReporterExecute {

  static Logger LOGGER = Logger.getLogger(SonarIssueReporterExecute.class.getName());

  static String debug_path = "C:\\dev\\repos\\arburk\\codeql2sonar-maven-plugin\\src\\test\\resources\\debug\\";

  public static void main(String[] args) throws IOException, URISyntaxException {
    final SonarIssueMapper sonarIssueMapper = new SonarIssueMapper();
    /*
    final URL systemResource = ClassLoader.getSystemResource("example.sarif");
    final File sarifInputFile = new File(systemResource.toURI());
    */
    final File sarifInputFile = new File(debug_path + "java-builtin.sarif");

    SarifParser.execute(sarifInputFile, logParser, sonarIssueMapper);
    LOGGER.info(sonarIssueMapper.getSummary());
    final StringWriter stringWriter = new StringWriter();
    final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    gson.toJson(sonarIssueMapper.getMappedIssues(null), stringWriter);
    LOGGER.info(stringWriter.toString());
    try (final FileWriter writer = new FileWriter(debug_path + "test.json")) {
      gson.toJson(sonarIssueMapper.getMappedIssues(new String[]{"/test/"}), writer);
      writer.flush();
    }
  }

  private static ParserCallback logParser = new ParserCallback() {
    @Override
    public void onFinding(Result result) {
      LOGGER.info(result.toString());
    }

    @Override
    public void onVersion(String version) {
      LOGGER.info(version);
    }

    @Override
    public void onSchema(String schema) {
      LOGGER.info(schema);
    }

    @Override
    public void onDriver(Driver driver) {
      LOGGER.info(driver.toString());
    }

    @Override
    public void onRule(Rule rule) {
      LOGGER.info(rule.toString());
    }
  };
}
