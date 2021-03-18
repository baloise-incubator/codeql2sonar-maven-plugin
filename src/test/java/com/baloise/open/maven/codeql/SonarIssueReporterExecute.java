package com.baloise.open.maven.codeql;

import com.baloise.open.maven.codeql.sarif.ParserCallback;
import com.baloise.open.maven.codeql.sarif.SarifParser;
import com.baloise.open.maven.codeql.sarif.dto.Driver;
import com.baloise.open.maven.codeql.sarif.dto.Result;
import com.baloise.open.maven.codeql.sarif.dto.Rule;
import com.baloise.open.maven.sonar.SonarIssueMapper;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

public class SonarIssueReporterExecute {

  static Logger LOGGER = Logger.getLogger(SonarIssueReporterExecute.class.getName());

  public static void main(String[] args) throws FileNotFoundException, URISyntaxException {
    final SonarIssueMapper sonarIssueMapper = new SonarIssueMapper();
    final URL systemResource = ClassLoader.getSystemResource("example.sarif");
    final File sarifInputFile = new File(systemResource.toURI());

    SarifParser.execute(sarifInputFile, logParser, sonarIssueMapper);
    LOGGER.info(sonarIssueMapper.getSummary());
    final StringWriter stringWriter = new StringWriter();
    new Gson().toJson(sonarIssueMapper.getMappedIssues(), stringWriter);
    LOGGER.info(stringWriter.toString());
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
