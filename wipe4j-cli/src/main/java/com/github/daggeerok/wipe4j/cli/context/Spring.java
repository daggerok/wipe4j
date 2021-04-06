package com.github.daggeerok.wipe4j.cli.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class Spring implements ApplicationContextAware {

  private static ApplicationContext context;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    // synchronized (this) { // synchronized (Spring.class) {
    if (Spring.context == null) Spring.context = applicationContext;
    // }
  }

  public static <T> T getBean(Class<T> type) {
    return context.getBean(type);
  }
}
