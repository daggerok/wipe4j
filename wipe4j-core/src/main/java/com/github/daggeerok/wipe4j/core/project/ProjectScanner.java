package com.github.daggeerok.wipe4j.core.project;

import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Log4j2
public class ProjectScanner {

  public Collection<Path> findAll(Path... baseDirectories) {
    return Arrays.stream(baseDirectories)
                 .map(ProjectScanner::find)
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

    var list = Try.of(() -> Files.find(baseDir, Integer.MAX_VALUE, searchPredicate)
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
