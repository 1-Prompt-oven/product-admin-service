package org.example.productadminservice.product.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ProductContent {

	private String contentUrl;

	private int contentOrder;

	private String sampleValue;

}
