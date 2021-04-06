package com.github.daggeerok.wipe4j.core;

import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
public class Projects {

  public boolean removeAll(Iterable<Path> paths) {
    var dryRunProperty = Optional.ofNullable(System.getProperty("wipe4j.dryRun"))
                                 .orElseThrow(() -> new RuntimeException("missing wipe4j.dryRun system property"));
    return Boolean.parseBoolean(dryRunProperty)
        && StreamSupport.stream(paths.spliterator(), false)
                        .map(path -> Try.run(() -> Files.walk(path)
                                                        .sorted(Comparator.reverseOrder())
                                                        .map(Path::toFile)
                                                        .forEachOrdered(File::delete)))
                        .filter(Try::isSuccess)
                        .count() > 0;
  }

  public Collection<Path> findAll(Path... baseDirectories) {
    return Arrays.stream(baseDirectories)
                 .map(Projects::find)
                 .flatMap(Collection::stream)
                 .collect(Collectors.toList());
  }

  private static final BiPredicate<Path, BasicFileAttributes> searchPredicate = (path, basicFileAttributes) -> {

    if (basicFileAttributes.isSymbolicLink() || basicFileAttributes.isRegularFile()) return false;

    var parentPath = path.getParent();
    if (Objects.isNull(parentPath)) return false;

    var parent = parentPath.toFile();
    var siblings = parent.list();
    if (Objects.isNull(siblings)) return false;

    var file = path.toFile();
    var filename = file.getName();
    return Arrays.stream(ProjectDefinition.values())
                 .filter(Predicate.not(ProjectDefinition.UNDEFINED::equals))
                 .filter(pd -> pd.wipeDirectories.contains(filename))
                 .filter(pd -> Arrays.stream(siblings).anyMatch(pd.searchFiles::contains))
                 .count() > 0;
  };

  private static Collection<Path> find(Path baseDir) {

    var exclusionsProperty = Optional.ofNullable(System.getProperty("wipe4j.exclusions"))
                                     .filter(Predicate.not(String::isBlank))
                                     .orElse("wipe4j.exclusions is u-n-d-e-f-i-n-e-d");
    var exclusions = Arrays.asList(exclusionsProperty.split(","));

    var list = Try.of(() -> Files.find(baseDir, Integer.MAX_VALUE, searchPredicate)
                                 .filter(path -> exclusions.stream().noneMatch(path.toFile().getAbsolutePath()::contains))
                                 .sorted(Comparator.comparing(path -> path.toFile().getAbsolutePath()))
                                 .sorted(Comparator.comparing(path -> path.toFile().getAbsolutePath().length()))
                                 .collect(Collectors.toList()))
                  .onFailure(log::error)
                  .get();

    var result = new ArrayList<Path>();

    for (Path path : list) {
      var absolutePath = path.toFile().getAbsolutePath();
      var unique = result.stream().noneMatch(p -> absolutePath.indexOf(p.toFile().getAbsolutePath()) == 0);
      if (unique) result.add(path.normalize());
    }

    return result;
  }
}
