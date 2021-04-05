package com.github.daggeerok.wipe4j.core.project;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@DisplayName("A projects scanner tests")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ProjectScannerTest {

  @Test
  void test() {
    // given
    var projectScanner = new ProjectScanner();
    // and
    var baseDir = Path.of(System.getProperty("user.dir"), "src", "test", "resources", "ProjectScannerTest")
                      .normalize();
    // when
    var projects = projectScanner.findAll(baseDir);
    // then
    log.info(projects);
    assertThat(projects).isNotNull()
                        .isNotEmpty()
                        .hasSize(1);
  }
}
