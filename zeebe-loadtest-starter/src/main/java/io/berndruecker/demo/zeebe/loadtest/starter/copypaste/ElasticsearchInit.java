package io.berndruecker.demo.zeebe.loadtest.starter.copypaste;

import java.net.URI;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchInit {

  @Value("${elasticsearch.url:#{null}}")
  private String elasticsearchUrl;

  @Bean
  public RestHighLevelClient createElasticClient() {
    if (elasticsearchUrl==null) {
      System.out.println("Skipping ElasticSearch as no URL is configured");
      return null;
    }
    try {
      System.out.println("Connect to ElasticSearch at '" + elasticsearchUrl + "'...");
  
      URI uri = new URI(elasticsearchUrl); 
      RestHighLevelClient client = new RestHighLevelClient( //
          RestClient.builder(new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme())));
      
      return client;
    } catch (Exception ex) {
      System.out.println("could not connect to elastic - ignoring");
      ex.printStackTrace();
      return null;
    }
  }
}
