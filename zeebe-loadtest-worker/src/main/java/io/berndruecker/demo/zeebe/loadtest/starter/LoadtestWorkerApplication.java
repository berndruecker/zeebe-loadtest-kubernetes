package io.berndruecker.demo.zeebe.loadtest.starter;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.commands.Workflow;

@SpringBootApplication
@Configuration
public class LoadtestWorkerApplication {
  
  @Value("${zeebe.brokerContactPoint}")
  private String zeebeBrokerContactPoint;
  
  @Bean
  public ZeebeClient zeebe() {
    System.out.println("Connect to Zeebe at '" + zeebeBrokerContactPoint + "'...");
    
    // Cannot yet use Spring Zeebe in current alpha
    ZeebeClient zeebeClient = ZeebeClient.newClientBuilder() //
        .brokerContactPoint(zeebeBrokerContactPoint) //
        .build();
    
    // check if workflow is already deployed:
    List<Workflow> workflows = zeebeClient.workflowClient()
      .newWorkflowRequest()
      .send().join()
      .getWorkflows();
    if (workflows.size()==0) {
      // Trigger deployment
      zeebeClient.workflowClient().newDeployCommand() //
      .addResourceFromClasspath("simple-workflow.bpmn") //
      .send().join();      
    }
    
    System.out.println("...connected");
    
    return zeebeClient;
  }

  public static void main(String[] args) throws Exception {
    ConfigurableApplicationContext applicationContext = //
        SpringApplication.run(LoadtestWorkerApplication.class, args);    
  }

}
