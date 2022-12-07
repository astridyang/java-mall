package com.example.test;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sally
 * @date 2022-09-16 17:01
 */
@Configuration
public class ElasticSearchConfig {
	@Bean
	public RestClient esRestClient() {
		return RestClient.builder(
				new HttpHost("http://192.168.62.31", 9200)).build();
	}

	@Bean
	public ElasticsearchClient esTransportClient() {
		RestClient restClient = RestClient.builder(HttpHost.create("http://192.168.62.31:9200")).build();

// Create the transport with a Jackson mapper
		ElasticsearchTransport transport = new RestClientTransport(
				restClient, new JacksonJsonpMapper());

// And create the API client
		return new ElasticsearchClient(transport);
	}
}
