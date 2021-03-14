package com.baloise.open.maven.codeql.sarif;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;

public class SarifParser {

  private static final String ELEMENT_VERSION = "version";

  public static void execute(File sarifInputFile, ParserCallback... callback) throws FileNotFoundException {

    final JsonObject rootObject = JsonParser.parseReader(new FileReader(sarifInputFile)).getAsJsonObject();
    if (rootObject.has(ELEMENT_VERSION)) {
      final String version = rootObject.get("version").getAsString();
      Arrays.stream(callback).forEach(cb -> cb.onVersion(version));
    }

  }

}
