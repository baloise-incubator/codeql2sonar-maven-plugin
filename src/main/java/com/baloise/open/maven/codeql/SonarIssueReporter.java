package com.baloise.open.maven.codeql;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo( name = "SonarIssueReporter", defaultPhase = LifecyclePhase.VERIFY)
public class SonarIssueReporter extends AbstractMojo {

  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("SonarIssueReporter works!");



  }

}
