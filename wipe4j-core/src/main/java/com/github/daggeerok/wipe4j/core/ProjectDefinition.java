package com.github.daggeerok.wipe4j.core;

import java.util.List;

public enum ProjectDefinition {

  MAVEN("maven",
        List.of("pom.xml", "settings.xml", "mvnw", "mvnw.cmd"),
        List.of("target"))
  ,
  GRADLE("gradle",
         List.of("build.grade", "build.grade.kts", "settings.grade", "settings.grade.kts", "gradle.properties", "gradlew", "gradlew.bat"),
         List.of("build", ".gradle"))
  ,
  IDEA("idea",
       List.of(".iml", ".ipr", ".iws"),
       List.of("out"))
  ,
  UNDEFINED("undefined", List.of(), List.of()),
  ;

  public final String type;
  public final List<String> searchFiles;
  public final List<String> wipeDirectories;

  ProjectDefinition(String type, List<String> searchFiles, List<String> wipeDirectories) {
    this.type = type;
    this.searchFiles = searchFiles;
    this.wipeDirectories = wipeDirectories;
  }
}
