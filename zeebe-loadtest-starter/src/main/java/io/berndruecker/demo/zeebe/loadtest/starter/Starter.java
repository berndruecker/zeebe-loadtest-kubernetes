package io.berndruecker.demo.zeebe.loadtest.starter;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.berndruecker.demo.zeebe.loadtest.starter.copypaste.MeasurementCollector;
import io.zeebe.client.ZeebeClient;

@Component
public class Starter {

  @Autowired
  private ZeebeClient zeebeClient;

  @Autowired
  private MeasurementCollector measure;

  @PostConstruct
  public void go() {
    measure.start();
    while (true) {
      startInstance("{\"hello\":\"test\"}");
      measure.increment();
    }
  }

  private void startInstance(String payload) {
    try {
      zeebeClient.workflowClient().newCreateInstanceCommand() //
        .bpmnProcessId("sample-load-generation-workflow") //
        .latestVersion() //
        .payload(payload) //
        .send().join();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  
}
