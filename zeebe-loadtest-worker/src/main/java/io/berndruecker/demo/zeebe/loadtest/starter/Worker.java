package io.berndruecker.demo.zeebe.loadtest.starter;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.berndruecker.demo.zeebe.loadtest.starter.copypaste.MeasurementCollector;
import io.zeebe.client.ZeebeClient;

@Component
public class Worker {
  
  @Autowired
  private ZeebeClient zeebeClient;

  @Autowired
  private MeasurementCollector measure;

  @PostConstruct
  public void go() {
    measure.start();
    
    zeebeClient.jobClient()
        .newWorker()
        .jobType("some-work")
        .handler((jobClient, job) -> {
          jobClient
              .newCompleteCommand(job.getKey())
              .send().join();
          measure.increment();
        }).open();
  } 
  
}