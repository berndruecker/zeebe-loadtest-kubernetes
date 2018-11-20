package io.berndruecker.demo.zeebe.loadtest.starter;

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

  @Bean
  public ZeebeClient zeebe() {
    System.out.println("Connect to Zeebe at '" + zeebeBrokerContactPoint + "'...");

    // Cannot yet use Spring Zeebe in current alpha
    ZeebeClient zeebeClient = ZeebeClient.newClientBuilder() //
        .brokerContactPoint(zeebeBrokerContactPoint) //
        .build();

    // check if workflow is already deployed:
    List<Workflow> workflows = zeebeClient.workflowClient() //
        .newWorkflowRequest() //
        .send().join() //
        .getWorkflows();
    
    boolean workflowDeployed = workflows.stream()
        .anyMatch(workflow -> workflow.getBpmnProcessId().equals("sample-load-generation-workflow"));
    
    if (!workflowDeployed) {
      // Trigger deployment
      zeebeClient.workflowClient().newDeployCommand() //
          .addResourceFromClasspath("sample-load-generation-workflow.bpmn") //
          .send().join();
      System.out.println("...deployed workflow definition successfully...");
    }

    System.out.println("...connected.");

    return zeebeClient;
  }
}
