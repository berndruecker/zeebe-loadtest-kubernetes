package io.berndruecker.demo.zeebe.loadtest.starter;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zeebe.client.ZeebeClient;

@Component
public class Worker {
  
  static AtomicInteger amount = new AtomicInteger();
  
  @Autowired
  private ZeebeClient zeebeClient;

  @PostConstruct
  public void go() {
    String type = "some-work";
    measureTime(type );
    
    zeebeClient.jobClient()
        .newWorker()
        .jobType(type)
        .handler((jobClient, job) -> {
          jobClient
              .newCompleteCommand(job.getKey())
              .send().join();
          amount.incrementAndGet();
        }).open();
  }
  
  private void measureTime(String type) {
    Timer t = new Timer();
    t.schedule(new TimerTask() {
      @Override
      public void run() {
        Date date = new Date();
        int currentAmount = amount.getAndSet(0);
        System.out.println(date.toString() + ": " + currentAmount);
      }
    }, 0, 10000);
  }
}