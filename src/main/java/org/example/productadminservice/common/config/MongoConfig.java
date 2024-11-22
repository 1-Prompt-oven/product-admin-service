package org.example.productadminservice.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

	@Override
	protected String getDatabaseName() {
		return "prompt_oven";
	}

	@Override
	public MongoClient mongoClient() {
		return MongoClients.create("mongodb://localhost:27017");
	}

	// 보안 설정 비활성화
	@Override
	protected boolean autoIndexCreation() {
		return true;
	}

	// @Bean
	// public AuditorAware<String> auditorProvider() {
	// 	return () -> Optional.of("system");
	// }
}