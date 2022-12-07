package com.example.test;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@SpringBootTest
class TestApplicationTests {
	@Resource
	private ElasticsearchClient esClient;

	@Test
	void contextLoads() {
	}

	@Test
	public void testTermsAgg() throws IOException {
		String searchText = "bike";
		Query query = MatchQuery.of(m -> m.field("name").query(FieldValue.of(searchText)))._toQuery();
		// TermsAggregation.Builder builder = new TermsAggregation.Builder();
		// builder.field("price").size(10);
		DoubleTermsAggregate.Builder builder = new DoubleTermsAggregate.Builder();
		SearchResponse<Void> response = esClient.search(s -> s
				.index("products")
				.size(0)
				.query(query)
				.aggregations("price-agg", a -> a.terms(t -> t.field("price").size(50))), Void.class);
		Aggregate aggregate = response.aggregations().get("price-agg");
		// Result of a terms aggregation when the field is some kind of decimal number like a float, double, or distance.
		List<DoubleTermsBucket> buckets = aggregate.dterms().buckets().array();
		for (DoubleTermsBucket bucket : buckets) {
			double key = bucket.key();
			System.out.println("bucket = " + bucket);
		}
		System.out.println("response = " + response);

	}

}
