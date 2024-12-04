package org.example.productadminservice.product.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Document(collection = "products")
public class Product {

	@Id
	private String id;

	@Indexed(unique = true)
	private String productUuid;

	private String sellerUuid;

	@TextIndexed
	private String productName;

	// 통화체계에 따라 유연하게 하게끔 Double. 금액은 무조건 double.
	// 정산관련할때 수익률 같은거 하게끔하면 반올림 관련해서도 애매해지는 부분이 있으니 double 쓰는게 좋음.
	private double price;

	// 암호화 고려하더라도 길이를 더 길게 쓰자.
	private String prompt;

	private String description;

	private Long llmId;

	private String topCategoryUuid;

	private String subCategoryUuid;

	private boolean deleted;

	private List<ProductContent> contents;

	private float discountRate;

	private boolean enabled;

	private boolean approved;

	private String seed;

	private Long llmVersionId;

	private double avgStar;

	@CreatedDate
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	private Long sells;

	private Long likeCount;

	private Long reviewCount;

	private boolean temporaryEnrolled;
}
