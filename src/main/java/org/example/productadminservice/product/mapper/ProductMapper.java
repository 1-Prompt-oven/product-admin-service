package org.example.productadminservice.product.mapper;

import org.example.productadminservice.common.utils.UuidGenerator;
import org.example.productadminservice.product.domain.Product;
import org.example.productadminservice.product.dto.in.AddProductRequestDto;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

	public Product createProduct(AddProductRequestDto addProductRequestDto, String encryptedPrompt) {

		return Product.builder()
			.sellerUuid(addProductRequestDto.getSellerUuid())
			.productUuid(addProductRequestDto.getProductUuid())
			.productName(addProductRequestDto.getProductName())
			.price(addProductRequestDto.getPrice())
			.prompt(encryptedPrompt)
			.description(addProductRequestDto.getDescription())
			.llmId(addProductRequestDto.getLlmId())
			.topCategoryUuid(addProductRequestDto.getTopCategoryUuid())
			.subCategoryUuid(addProductRequestDto.getSubCategoryUuid())
			.contents(addProductRequestDto.getContents())
			.enabled(true)
			.approved(true)
			.deleted(false)
			.discountRate(addProductRequestDto.getDiscountRate())
			.llmVersionId(addProductRequestDto.getLlmVersionId())
			.sells(0L)
			.avgStar(0.0)
			.reviewCount(0L)
			.likeCount(0L)
			.temporaryEnrolled(addProductRequestDto.isTemporaryEnrolled())
			.build();
	}
}
