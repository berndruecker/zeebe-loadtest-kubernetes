package io.berndruecker.demo.zeebe.loadtest.starter;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.berndruecker.demo.zeebe.loadtest.starter.copypaste.MeasurementCollector;
import io.zeebe.client.ZeebeClient;

@Component
public class Worker {
  
  @Autowired
  private ZeebeClient zeebeClient;

  @Autowired
  private MeasurementCollector measure;
  
  @Value("${loadtest.workExecutionTimeInMillis:0}")
  private int workExecutionTimeInMillis = 0;

  @Value("${loadtest.jobType:some-work}")
  private List<String> jobTypes = Collections.singletonList("some-work");  

  @PostConstruct
  public void go() {
    measure.start();
    
    jobTypes.forEach( jobType ->
      zeebeClient.jobClient() //
          .newWorker() //
          .jobType(jobType) // 
          .handler((jobClient, job) -> {
            
            // Simulate time passing while executing work
            try {
              Thread.sleep(workExecutionTimeInMillis);
            } catch (InterruptedException e) {
              // ignore
            }
            
            // complete the task
            jobClient
                .newCompleteCommand(job.getKey())
                .send().join();
            
            // and move on
            measure.increment();
          })
          .bufferSize(1)
          .open()
       );
  } 
  
}