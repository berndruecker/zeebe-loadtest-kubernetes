package io.berndruecker.demo.zeebe.loadtest.starter;

import java.net.URI;
import java.util.List;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.commands.Workflow;

@SpringBootApplication
@Configuration
public class LoadtestStarterApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(LoadtestStarterApplication.class, args);
  }

}
