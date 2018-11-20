package io.berndruecker.demo.zeebe.loadtest.starter;

import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zeebe.client.ZeebeClient;

@Component
public class Starter {

  static AtomicInteger amount = new AtomicInteger();

  @Autowired
  private ZeebeClient zeebeClient;

  @Autowired
  private RestHighLevelClient elasticClient;

  @PostConstruct
  public void go() {
    measureTime();
    while (true) {
      startInstance("{\"hello\":\"test\"}");
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
    amount.incrementAndGet();
  }

  private void measureTime() {
    Timer t = new Timer();
    t.schedule(new TimerTask() {
      @Override
      public void run() {
        Date date = new Date();
        int count = amount.getAndSet(0);
        sendToElastic(date, count);
        System.out.println(date.toString() + ": " + count);
      }

    }, 0, 10000);
  }

  public void sendToElastic(Date date, int count) {
    try {
      if (elasticClient != null) {

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("timestamp", date);
        jsonMap.put("type", "started");
        jsonMap.put("hostname", InetAddress.getLocalHost().getHostName());
        jsonMap.put("count", count);

        IndexRequest indexRequest = new IndexRequest(//
            "zeebe-load-metrics", //
            "doc", //
            UUID.randomUUID().toString()) //
                .source(jsonMap);

        elasticClient.index(indexRequest, RequestOptions.DEFAULT);
      } else {
        System.out.print(".Could not send metrics to elastic, not connected.");
      }
    } catch (Exception ex) {
      System.out.println("Could not send metrics to elastic due to exception");
      ex.printStackTrace();
    }
  }
}
