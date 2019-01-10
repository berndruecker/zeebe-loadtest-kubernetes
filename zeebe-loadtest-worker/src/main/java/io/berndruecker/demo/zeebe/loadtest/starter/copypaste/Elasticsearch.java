package io.berndruecker.demo.zeebe.loadtest.starter.copypaste;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Elasticsearch {

  @Value("${elasticsearch.url:#{null}}")
  private String elasticsearchUrl;

  private RestHighLevelClient elasticClient;

  public void sendToElastic(String type, Map<String, Object> jsonMap) {
    if (elasticClient==null) {
      return;
    }
    try {
      IndexRequest indexRequest = new IndexRequest(//
          "zeebe-load-metrics-" + type, //
          "_doc", //
          UUID.randomUUID().toString()) //
              .source(jsonMap);

      elasticClient.index(indexRequest, RequestOptions.DEFAULT);
    } catch (Exception ex) {
      System.out.println("Could not send metrics to elastic due to exception"); // - diabling it");
      ex.printStackTrace();
//      elasticClient = null;
    }
  }

  @PostConstruct
  public void createElasticClient() {
    if (elasticsearchUrl == null) {
      System.out.println("Skipping ElasticSearch as no URL is configured");
      return;
    }
    try {
      System.out.println("Connect to ElasticSearch at '" + elasticsearchUrl + "'...");

      URI uri = new URI(elasticsearchUrl);
      elasticClient = new RestHighLevelClient( //
          RestClient.builder(new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme())));
    } catch (Exception ex) {
      System.out.println("could not connect to elastic - ignoring");
      ex.printStackTrace();
    }
  }
}
