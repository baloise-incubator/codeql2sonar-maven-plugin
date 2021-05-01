package com.baloise.open.maven.codeql.sarif;

import com.baloise.open.maven.codeql.sarif.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SarifParserTest {

  @Captor
  ArgumentCaptor<String> versionCaptor;

  @Captor
  ArgumentCaptor<String> schemaCaptor;

  @Captor
  ArgumentCaptor<Driver> driverCaptor;

  @Captor
  ArgumentCaptor<Rule> ruleCaptor;

  @Captor
  ArgumentCaptor<Result> resultCaptor;

  @Test
  void execute_testFile_HappyCase() throws URISyntaxException, FileNotFoundException {
    final ParserCallback mockedParserCB = Mockito.mock(ParserCallback.class);
    final File exampleSarifFile = new File(ClassLoader.getSystemResource("example.sarif").toURI());

    SarifParser.execute(exampleSarifFile, mockedParserCB);

    Mockito.verify(mockedParserCB, Mockito.times(1)).onVersion(versionCaptor.capture());
    assertEquals("2.1.0", versionCaptor.getValue());

    Mockito.verify(mockedParserCB, Mockito.times(1)).onSchema(schemaCaptor.capture());
    assertEquals("https://raw.githubusercontent.com/oasis-tcs/sarif-spec/master/Schemata/sarif-schema-2.1.0.json", schemaCaptor.getValue());

    Mockito.verify(mockedParserCB, Mockito.times(1)).onDriver(driverCaptor.capture());
    final Driver driverCaptured = driverCaptor.getValue();
    assertEquals("GitHub", driverCaptured.getOrganization());
    assertEquals("CodeQL", driverCaptured.getName());
    assertEquals("2.3.3", driverCaptured.getSemanticVersion());

    Mockito.verify(mockedParserCB, Mockito.times(10)).onRule(ruleCaptor.capture());
    final List<Rule> rulesCaptured = ruleCaptor.getAllValues();
    final Rule emptySynchBlock = rulesCaptured.get(2);
    assertEquals("java/empty-synchronized-block", emptySynchBlock.getId());
    assertEquals("java/empty-synchronized-block", emptySynchBlock.getName());
    assertEquals("Empty synchronized block", emptySynchBlock.getShortDescription());
    assertEquals("Empty synchronized blocks may indicate the presence of incomplete code or incorrect synchronization, and may lead to concurrency problems.", emptySynchBlock.getFullDescription());
    assertNull(emptySynchBlock.getLevel());
    assertNotNull(emptySynchBlock.getProperties());
    final RuleProperties esbProperties = emptySynchBlock.getProperties();
    assertEquals("java/empty-synchronized-block", esbProperties.getId());
    assertEquals("Empty synchronized block", esbProperties.getName());
    assertEquals("Empty synchronized blocks may indicate the presence of\n              incomplete code or incorrect synchronization, and may lead to concurrency problems.", esbProperties.getDescription());
    assertEquals("low", esbProperties.getPrecision());
    assertEquals("problem", esbProperties.getKind());
    assertEquals(RuleProperties.Severity.warning, esbProperties.getSeverity());
    final String[] esbTags = esbProperties.getTags();
    assertEquals(5, esbTags.length);
    assertEquals(1, Arrays.stream(esbTags).filter("reliability"::equals).count());
    assertEquals(1, Arrays.stream(esbTags).filter("correctness"::equals).count());
    assertEquals(1, Arrays.stream(esbTags).filter("concurrency"::equals).count());
    assertEquals(1, Arrays.stream(esbTags).filter("language-features"::equals).count());
    assertEquals(1, Arrays.stream(esbTags).filter("external/cwe/cwe-585"::equals).count());

    final Rule impossibleArrayCast = rulesCaptured.get(3);
    assertEquals("java/impossible-array-cast", impossibleArrayCast.getId());
    assertEquals("java/impossible-array-cast", impossibleArrayCast.getName());
    assertEquals("Impossible array cast", impossibleArrayCast.getShortDescription());
    assertEquals("Trying to cast an array of a particular type as an array of a subtype causes a 'ClassCastException' at runtime.", impossibleArrayCast.getFullDescription());
    assertNotNull(impossibleArrayCast.getLevel());
    assertEquals(Rule.Level.ERROR, impossibleArrayCast.getLevel());
    assertNotNull(impossibleArrayCast.getProperties());
    final RuleProperties icProperties = impossibleArrayCast.getProperties();
    assertEquals("java/impossible-array-cast", icProperties.getId());
    assertEquals("Impossible array cast", icProperties.getName());
    assertEquals("Trying to cast an array of a particular type as an array of a subtype causes a\n              'ClassCastException' at runtime.", icProperties.getDescription());
    assertEquals("low", icProperties.getPrecision());
    assertEquals("problem", icProperties.getKind());
    assertEquals(RuleProperties.Severity.error, icProperties.getSeverity());
    final String[] icTags = icProperties.getTags();
    assertEquals(4, icTags.length);
    assertEquals(1, Arrays.stream(icTags).filter("reliability"::equals).count());
    assertEquals(1, Arrays.stream(icTags).filter("correctness"::equals).count());
    assertEquals(1, Arrays.stream(icTags).filter("logic"::equals).count());
    assertEquals(1, Arrays.stream(icTags).filter("external/cwe/cwe-704"::equals).count());

    Mockito.verify(mockedParserCB, Mockito.times(1)).onFinding(resultCaptor.capture());
    final Result result = resultCaptor.getValue();
    assertEquals("java/misleading-indentation", result.getRuleId());
    assertEquals(9, result.getRuleIndex());
    assertEquals("Indentation suggests that [the next statement](1) belongs to [the control structure](2), but this is not the case; consider adding braces or adjusting indentation.", result.getMessage());
    assertNotNull(result.getLocations());
    assertEquals(1, result.getLocations().size());
    final Location location = result.getLocations().get(0);
    assertEquals("src/main/java/org/arburk/fishbone/infrastructure/service/FishRepository.java", location.getUri());
    assertEquals("%SRCROOT%", location.getUriBaseId());
    assertEquals(0, location.getIndex());
    assertNotNull(location.getRegion());
    final Region region = location.getRegion();
    assertEquals(26, region.getStartLine());
    assertEquals(9, region.getStartColumn());
    assertEquals(13, region.getEndColumn());
  }

  @Test
  void execute_wrongFile_FNFException() {
    assertThrows(FileNotFoundException.class, () -> SarifParser.execute(new File(""), (ParserCallback) null));
  }

}
