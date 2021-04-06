package com.github.daggeerok.wipe4j.cli.context;

import com.github.daggeerok.wipe4j.core.Projects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Wipe4jConfig {

  @Bean
  @ConditionalOnMissingBean
  Projects projects() {
    return new Projects();
  }
}
