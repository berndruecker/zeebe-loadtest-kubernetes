package io.berndruecker.demo.zeebe.loadtest.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class LoadtestWorkerApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(LoadtestWorkerApplication.class, args);
  }

}
