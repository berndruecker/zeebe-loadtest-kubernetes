package io.berndruecker.demo.zeebe.loadtest.starter.copypaste;

import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.commands.Workflow;

@Configuration
public class ZeebeInit {
  
  @Value("${zeebe.brokerContactPoint}")
  private String zeebeBrokerContactPoint;

  @Value("${zeebe.numberOfWorkerThreads:1}")
  private int numberOfWorkerThreads = 1;    
    
  @Bean
  public ZeebeClient zeebe() throws Exception {
    System.out.println("Connect to Zeebe at '" + zeebeBrokerContactPoint + "'...");

    // Cannot yet use Spring Zeebe in current alpha
    ZeebeClient zeebeClient = ZeebeClient.newClientBuilder() //
        .brokerContactPoint(zeebeBrokerContactPoint) //
        .numJobWorkerExecutionThreads(numberOfWorkerThreads) //
        .build();

    System.out.println("...connected.");

    return zeebeClient;
  }
}
