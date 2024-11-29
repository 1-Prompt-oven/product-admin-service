package org.example.productadminservice.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoConfig {
	@Bean
	public MongoCustomConversions mongoCustomConversions() {
		return MongoCustomConversions.create(config ->
			config.registerConverter(new BinaryToStringConverter()));
	}
}