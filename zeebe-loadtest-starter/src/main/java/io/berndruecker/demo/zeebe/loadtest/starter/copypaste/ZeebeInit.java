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

  @Value("${zeebe.numberOfWorkerThreads}")
  private int numberOfWorkerThreads = 1;    
  
  @Value("${loadtest.bpmnProcess}")
  private String bpmnProcessId = "sample-load-generation-workflow";

  @Value("${loadtest.bpmnProcessFileUrl}")
  private String bpmnProcessFileUrl = null;
  
  @Bean
  public ZeebeClient zeebe() throws Exception {
    System.out.println("Connect to Zeebe at '" + zeebeBrokerContactPoint + "'...");

    // Cannot yet use Spring Zeebe in current alpha
    ZeebeClient zeebeClient = ZeebeClient.newClientBuilder() //
        .brokerContactPoint(zeebeBrokerContactPoint) //
        .numJobWorkerExecutionThreads(numberOfWorkerThreads) //
        .build();

    // check if workflow is already deployed:
    List<Workflow> workflows = zeebeClient.workflowClient() //
        .newWorkflowRequest() //
        .send().join() //
        .getWorkflows();
    
    boolean workflowDeployed = workflows.stream()
        .anyMatch(workflow -> workflow.getBpmnProcessId().equals(bpmnProcessId));
    
    if (!workflowDeployed) {
      if (bpmnProcessFileUrl!=null) { // deploy model from URL
        zeebeClient.workflowClient().newDeployCommand() //
          .addResourceStream(new URL(bpmnProcessFileUrl).openStream(), bpmnProcessId + ".bpmn") //
          .send().join();        
      } else { // deploy default model from classpath
        zeebeClient.workflowClient().newDeployCommand() //
          .addResourceFromClasspath("sample-load-generation-workflow.bpmn") //
          .send().join();        
      }
      System.out.println("...deployed workflow definition successfully...");
    }

    System.out.println("...connected.");

    return zeebeClient;
  }
}
