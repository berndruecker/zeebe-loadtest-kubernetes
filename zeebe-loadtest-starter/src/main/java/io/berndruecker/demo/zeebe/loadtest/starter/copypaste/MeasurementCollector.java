package io.berndruecker.demo.zeebe.loadtest.starter.copypaste;

import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MeasurementCollector {
  
  static AtomicInteger count = new AtomicInteger();
  
  @Value("${loadtest.type}")
  private String type;

  @Value("${loadtest.numberOfClientNodes}")
  private int numberOfClientNodes;

  @Value("${loadtest.numberOfBrokerNodes}")
  private int numberOfBrokerNodes;

  @Value("${loadtest.replicationFactor}")
  private int replicationFactor;

  @Value("${loadtest.numberOfPartitions}")
  private int numberOfPartitions;

  @Value("${loadtest.numberOfMachines}")
  private int numberOfMachines;

  @Value("${loadtest.machineType}")
  private String machineType;
  
  @Value("${loadtest.storage}")
  private String storage;
  
  @Autowired
  private RestHighLevelClient elasticClient;
  
  public void increment() {
    count.incrementAndGet();
  }  

  public void start() {
    Timer t = new Timer();
    t.schedule(new TimerTask() {
      @Override
      public void run() {
        Date date = new Date();
        int currentCount = count.getAndSet(0);
        sendToElastic(date, currentCount);
        sendToConsole(currentCount);
      }
    }, 0, 10000); // every 10 seconds
  }

  private void sendToConsole(int count) {
    System.out.println(count);
  }
  
  public void sendToElastic(Date date, int count) {
    try {
      if (elasticClient != null) {

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("timestamp", date);

        jsonMap.put("type", type);
        jsonMap.put("numberOfClientNodes", numberOfClientNodes);       
        jsonMap.put("numberOfBrokerNodes", numberOfBrokerNodes);        
        jsonMap.put("replicationFactor", replicationFactor);        
        jsonMap.put("numberOfMachines", numberOfMachines);        
        jsonMap.put("machineType", machineType);
        jsonMap.put("storage", storage);        
        
        jsonMap.put("hostname", InetAddress.getLocalHost().getHostName());
        jsonMap.put("count", count);

        IndexRequest indexRequest = new IndexRequest(//
            "zeebe-load-metrics-" + type, //
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
