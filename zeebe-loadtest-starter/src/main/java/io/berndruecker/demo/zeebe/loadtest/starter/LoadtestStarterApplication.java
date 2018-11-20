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

  @Value("${zeebe.brokerContactPoint}")
  private String zeebeBrokerContactPoint;

  @Value("${elasticsearch.url}")
  private String elasticsearchUrl;

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

  @Bean
  public RestHighLevelClient createElasticClient() {
    try {
      System.out.println("Connect to ElasticSearch at '" + elasticsearchUrl + "'...");
  
      URI uri = new URI(elasticsearchUrl); 
      RestHighLevelClient client = new RestHighLevelClient( //
          RestClient.builder(new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme())));
      
      System.out.println("...connected.");
      return client;
    } catch (Exception ex) {
      System.out.println("could not connect to elastic - ignoring");
      ex.printStackTrace();
      return null;
    }
  }

  public static void main(String[] args) throws Exception {
    SpringApplication.run(LoadtestStarterApplication.class, args);
  }

}
