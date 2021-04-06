package com.github.daggeerok.wipe4j.cli.piclocli;

import io.vavr.control.Try;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface Command extends Callable<Integer> {

  void execute();

  @Override
  default Integer call() {
    var aTry = Try.run(this::execute)
                  .onFailure(throwable -> LogManager.getLogger().error(throwable::getLocalizedMessage));
    return aTry.isSuccess() ? 0 : -1;
  }
}
