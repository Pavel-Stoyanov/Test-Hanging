package com.example.test.elasticsearch;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import com.example.test.config.ConfigProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

public class GlobalES {
	
	private static final Logger log = LogManager.getLogger(GlobalES.class);
	
	private static ElasticsearchClient client = null;
	
	static {
      final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(ConfigProperties.getEsUser(), ConfigProperties.getEsPassword()));

      RestClientBuilder builder = RestClient
              .builder(new HttpHost(ConfigProperties.getEsHost(), ConfigProperties.getEsPort(), ConfigProperties.getEsProtocol()))
              .setHttpClientConfigCallback(httpClientBuilder -> {
                  httpClientBuilder.disableAuthCaching();
                  if (ConfigProperties.isEnableProxy()) {
                  	httpClientBuilder
                      .setProxy(new HttpHost(ConfigProperties.getProxyHost(), ConfigProperties.getProxyPort(), ConfigProperties.getProxyProtocol()));
                  }
                  return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
              }).setRequestConfigCallback(
                      arg0 -> arg0.setConnectTimeout(ConfigProperties.getEsConnectionTimeout()).setSocketTimeout(ConfigProperties.getEsSocketTimeout()));
      ObjectMapper mapper = new ObjectMapper();
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      ElasticsearchTransport transport = new RestClientTransport(builder.build(), new JacksonJsonpMapper(mapper));

      client = new ElasticsearchClient(transport);
	}
	
	public static ElasticsearchClient getClient() {
		return client;
	}
	
	public static void shutdown() {
    	try {
    		client._transport().close();
    		client.shutdown();
		} catch (Exception e) {
			log.error("Unexpected error during shutdown of ES client - ", e);
		}
    }
}
