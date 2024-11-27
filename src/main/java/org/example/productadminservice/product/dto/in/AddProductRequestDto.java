package org.example.productadminservice.product.dto.in;

import java.util.List;

import org.example.productadminservice.product.domain.ProductContent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AddProductRequestDto {

	private String productUuid;
	private String sellerUuid;
	private String productName;
	private double price;
	private String prompt;
	private String description;
	private Long llmId;
	private String topCategoryUuid;
	private String subCategoryUuid;
	private List<ProductContent> contents;
	private float discountRate;
	private Long llmVersionId;
	private boolean deleted;
}
