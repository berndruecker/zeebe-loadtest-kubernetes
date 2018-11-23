package io.berndruecker.demo.zeebe.loadtest.starter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.berndruecker.demo.zeebe.loadtest.starter.copypaste.MeasurementCollector;
import io.zeebe.client.ZeebeClient;

@Component
public class Starter {

  @Autowired
  private ZeebeClient zeebeClient;

  @Autowired
  private MeasurementCollector measure;

  @Value("${loadtest.payloadFileUrl:#{null}}")
  private String payloadFileUrl = null;
  
  @Value("${loadtest.bpmnProcess:sample-load-generation-workflow}")
  private String bpmnProcess="sample-load-generation-workflow";
  

  @PostConstruct
  public void go() throws Exception {
    String payload = "{\"hello\":\"world\"}"; // default
    if (payloadFileUrl != null) {
      payload = readFromUrl(payloadFileUrl);
    }

    measure.start();
    while (true) {
      startInstance(payload);
      measure.increment();
    }
  }

  private void startInstance(String payload) {
    try {
      zeebeClient.workflowClient().newCreateInstanceCommand() //
          .bpmnProcessId(bpmnProcess) //
          .latestVersion() //
          .payload(payload) //
          .send().join();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static String readFromUrl(String url) throws Exception {
    InputStream is = new URL(url).openStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

      StringBuilder sb = new StringBuilder();
      int cp;
      while ((cp = rd.read()) != -1) {
        sb.append((char) cp);
      }
      return sb.toString();
    } finally {
      is.close();
    }
  }

}
