// package com.example.search.config;
//
// import org.apache.http.HttpHost;
// import org.elasticsearch.client.RequestOptions;
// import org.elasticsearch.client.RestClient;
// import org.elasticsearch.client.RestHighLevelClient;
// import org.springframework.context.annotation.Bean;
//
// /**
//  * @author sally
//  * @date 2022-09-27 17:34
//  */
// public class GulimallElasticSearchConfig {
// 	public static final RequestOptions COMMON_OPTIONS;
// 	static {
// 		RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
// 		// builder.addHeader("Authorization", "Bearer " + TOKEN);
// 		// builder.setHttpAsyncResponseConsumerFactory(
// 		//         new HttpAsyncResponseConsumerFactory
// 		//                 .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
// 		COMMON_OPTIONS = builder.build();
// 	}
//
// 	@Bean
// 	public RestHighLevelClient restHighLevelClient(){
// 		return new RestHighLevelClient(
// 				// RestClient.builder(new HttpHost("192.168.62.31", 9200, "http")));
// 				RestClient.builder(HttpHost.create("http://192.168.62.31:9200")));
// 	}
// }
