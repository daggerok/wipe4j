package com.github.daggeerok.wipe4j.cli;

import com.github.daggeerok.wipe4j.cli.piclocli.FindAndCleanupCommand;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
public class CliApplication {
  public static void main(String[] args) {
    var applicationContext = SpringApplication.run(CliApplication.class);
    applicationContext.registerShutdownHook();

    var findAndCleanupCommand = applicationContext.getBean(FindAndCleanupCommand.class);
    var commandLine = new CommandLine(findAndCleanupCommand);
    var exitCode = commandLine.execute(args);
    SpringApplication.exit(applicationContext);
    System.exit(exitCode);
  }
}
