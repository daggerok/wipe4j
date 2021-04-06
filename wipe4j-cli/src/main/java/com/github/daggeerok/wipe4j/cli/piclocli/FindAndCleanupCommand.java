package com.github.daggeerok.wipe4j.cli.piclocli;

import com.github.daggeerok.wipe4j.cli.context.Spring;
import com.github.daggeerok.wipe4j.core.Projects;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Log4j2
@Component
@CommandLine.Command(
    name = "wipe4j",
    mixinStandardHelpOptions = true,
    description = "Search JVM projects build output directories")
public class FindAndCleanupCommand implements Command {

  @CommandLine.Parameters(description = "base directory for search", defaultValue = ".")
  private String baseDir;

  @CommandLine.Option(names = { "--dry-run" }, description = "dry run, enabled by default", defaultValue = "true")
  private boolean dryRun;

  @CommandLine.Option(
      names = { "--exclusions" }, split = ",", arity = "0..*", description = "paths for exclusions divided by coma",
      defaultValue = "test/resources/ProjectScannerTest/should_find_maven_project,wipe4j-cli/target,wipe4j-core/target"
  )
  private List<String> exclusions;

  @Override
  public void execute() {

    var base = Optional.of(baseDir)
                       .filter(Predicate.not("."::equals))
                       .map(Path::of)
                       .orElseGet(() -> Path.of(System.getProperty("user.dir")));

    var wipe4jProps = Map.of("wipe4j.baseDir", String.valueOf(base),
                             "wipe4j.dryRun", String.valueOf(dryRun),
                             "wipe4j.exclusions", String.join(",", exclusions));

    log.debug(() -> "wipe4j execution using next config props:");
    wipe4jProps.forEach((k, v) -> log.debug("{}={}", ()-> k, () -> v));
    System.getProperties().putAll(wipe4jProps);

    var projects = Spring.getBean(Projects.class);
    var jvmProjects = projects.findAll(base);
    log.info("remove has been completed successfully: {}", () -> projects.removeAll(jvmProjects));

    if (jvmProjects.isEmpty()) return;
    log.info(() -> "next projects has been processed during wipe4j execution:");
    jvmProjects.forEach(log::info);
  }
}
